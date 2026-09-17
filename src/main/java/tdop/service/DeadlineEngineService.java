package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.DeadlineReminder;
import tdop.entity.Opportunity;
import tdop.entity.enums.NotificationType;
import tdop.entity.enums.OpportunityStatus;
import tdop.notification.NotificationService;
import tdop.repository.DeadlineReminderRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.PlatformConfigRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadlineEngineService {

    private final DeadlineReminderRepository reminderRepository;
    private final OpportunityRepository opportunityRepository;
    private final PlatformConfigRepository configRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 3600000)
    public void processDeadlines() {
        log.info("Deadline engine: starting scheduled run");
        int expired = markExpiredOpportunities();
        int closingSoon = markClosingSoonOpportunities();
        int reminders = sendDeadlineReminders();
        log.info("Deadline engine: completed — expired={}, closingSoon={}, reminders={}", expired, closingSoon, reminders);
    }

    @Transactional
    public int markExpiredOpportunities() {
        List<Opportunity> expired = opportunityRepository.findExpiredNotMarked();
        int count = 0;
        for (Opportunity opp : expired) {
            try {
                opp.setStatus(OpportunityStatus.EXPIRED);
                opportunityRepository.save(opp);
                count++;
                log.info("Opportunity expired: id={} title='{}'", opp.getId(), opp.getTitle());
            } catch (Exception e) {
                log.error("Failed to expire opportunity id={}: {}", opp.getId(), e.getMessage());
            }
        }
        return count;
    }

    @Transactional
    public int markClosingSoonOpportunities() {
        int closingSoonDays = getConfigInt("opportunity.closing_soon_days", 7);
        LocalDateTime threshold = LocalDateTime.now().plusDays(closingSoonDays);
        List<Opportunity> published = opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED);
        int count = 0;
        for (Opportunity opp : published) {
            try {
                if (opp.getDeadline() != null
                    && opp.getDeadline().isBefore(threshold)
                    && opp.getDeadline().isAfter(LocalDateTime.now())
                    && !opp.isClosingSoonNotified()) {
                    opp.setStatus(OpportunityStatus.CLOSING_SOON);
                    opp.setClosingSoonNotified(true);
                    opportunityRepository.save(opp);
                    count++;
                    sendClosingSoonNotification(opp);
                    log.info("Opportunity closing soon: id={} deadline={}", opp.getId(), opp.getDeadline());
                }
            } catch (Exception e) {
                log.error("Failed to mark closing-soon opportunity id={}: {}", opp.getId(), e.getMessage());
            }
        }
        return count;
    }

    @Transactional
    public int sendDeadlineReminders() {
        List<Integer> reminderDays = getReminderDays();
        List<Opportunity> active = opportunityRepository.findByStatusIn(
            Arrays.asList(OpportunityStatus.PUBLISHED, OpportunityStatus.CLOSING_SOON));
        int count = 0;

        for (Opportunity opp : active) {
            try {
                if (opp.getDeadline() == null) continue;
                for (int days : reminderDays) {
                    LocalDateTime reminderDate = opp.getDeadline().minusDays(days);
                    if (LocalDateTime.now().isAfter(reminderDate) && LocalDateTime.now().isBefore(opp.getDeadline())) {
                        DeadlineReminder existing = reminderRepository
                            .findByOpportunityIdAndReminderDays(opp.getId(), days)
                            .orElse(null);
                        if (existing == null) {
                            DeadlineReminder reminder = DeadlineReminder.builder()
                                .opportunity(opp)
                                .reminderDays(days)
                                .sent(true)
                                .sentAt(LocalDateTime.now())
                                .build();
                            reminderRepository.save(reminder);
                            count++;
                            sendDeadlineReminderNotification(opp, days);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process deadline reminders for opportunity id={}: {}", opp.getId(), e.getMessage());
            }
        }
        return count;
    }

    private void sendClosingSoonNotification(Opportunity opp) {
        try {
            if (opp.getCreatedBy() != null && opp.getCreatedBy().getUser() != null) {
                Long orgUserId = opp.getCreatedBy().getUser().getId();
                notificationService.createNotification(
                    orgUserId,
                    "Opportunity Closing Soon",
                    "Your opportunity '" + opp.getTitle() + "' is approaching its deadline on " + opp.getDeadline(),
                    NotificationType.DEADLINE
                );
            }
        } catch (Exception e) {
            log.error("Failed to send closing-soon notification for opportunity id={}: {}", opp.getId(), e.getMessage());
        }
    }

    private void sendDeadlineReminderNotification(Opportunity opp, int days) {
        try {
            if (opp.getCreatedBy() != null && opp.getCreatedBy().getUser() != null) {
                Long orgUserId = opp.getCreatedBy().getUser().getId();
                notificationService.createNotification(
                    orgUserId,
                    "Deadline Reminder",
                    "'" + opp.getTitle() + "' deadline is in " + days + " day(s) (" + opp.getDeadline() + ")",
                    NotificationType.DEADLINE
                );
            }
        } catch (Exception e) {
            log.error("Failed to send deadline reminder notification for opportunity id={}: {}", opp.getId(), e.getMessage());
        }
    }

    public List<DeadlineReminder> getRemindersForOpportunity(Long opportunityId) {
        return reminderRepository.findByOpportunityId(opportunityId);
    }

    private List<Integer> getReminderDays() {
        String configValue = configRepository.findByConfigKey("deadline.reminder.days")
            .map(c -> c.getConfigValue())
            .orElse("7,3,1");
        return Arrays.stream(configValue.split(","))
            .map(String::trim)
            .map(s -> { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } })
            .filter(d -> d > 0)
            .collect(Collectors.toList());
    }

    private int getConfigInt(String key, int defaultValue) {
        return configRepository.findByConfigKey(key)
            .map(c -> {
                try { return Integer.parseInt(c.getConfigValue()); }
                catch (Exception e) { return defaultValue; }
            })
            .orElse(defaultValue);
    }
}
