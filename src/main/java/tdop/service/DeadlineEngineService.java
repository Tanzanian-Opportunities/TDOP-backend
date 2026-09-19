package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.DeadlineReminder;
import tdop.entity.Opportunity;
import tdop.entity.enums.OpportunityStatus;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.email.EmailService;
import tdop.repository.DeadlineReminderRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.PlatformConfigRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeadlineEngineService {

    private final DeadlineReminderRepository reminderRepository;
    private final OpportunityRepository opportunityRepository;
    private final PlatformConfigRepository configRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 3600000) // Every hour
    public void processDeadlines() {
        markExpiredOpportunities();
        markClosingSoonOpportunities();
        sendDeadlineReminders();
    }

    public void markExpiredOpportunities() {
        List<Opportunity> expired = opportunityRepository.findExpiredNotMarked();
        for (Opportunity opp : expired) {
            opp.setStatus(OpportunityStatus.EXPIRED);
            opportunityRepository.save(opp);
        }
    }

    public void markClosingSoonOpportunities() {
        int closingSoonDays = getConfigInt("opportunity.closing_soon_days", 7);
        LocalDateTime threshold = LocalDateTime.now().plusDays(closingSoonDays);
        List<Opportunity> published = opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED);
        for (Opportunity opp : published) {
            if (opp.getDeadline() != null && opp.getDeadline().isBefore(threshold) && opp.getDeadline().isAfter(LocalDateTime.now())) {
                opp.setStatus(OpportunityStatus.CLOSING_SOON);
                opp.setClosingSoonNotified(true);
                opportunityRepository.save(opp);
            }
        }
    }

    public void sendDeadlineReminders() {
        List<Integer> reminderDays = getReminderDays();
        List<Opportunity> active = opportunityRepository.findByStatusIn(
            Arrays.asList(OpportunityStatus.PUBLISHED, OpportunityStatus.CLOSING_SOON));

        for (Opportunity opp : active) {
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
                            .sent(false)
                            .build();
                        reminderRepository.save(reminder);
                        sendReminderEmail(opp, days);
                    }
                }
            }
        }
    }

    private void sendReminderEmail(Opportunity opp, int days) {
        if (opp.getCreatedBy() == null || opp.getCreatedBy().getUser() == null) return;
        try {
            String email = opp.getCreatedBy().getUser().getEmail();
            String subject = "Deadline Reminder: " + opp.getTitle() + " - " + days + " day(s) left";
            String body = "<h2>Deadline Approaching</h2>"
                + "<p>The opportunity <strong>" + opp.getTitle() + "</strong> has " + days + " day(s) remaining before its deadline.</p>"
                + "<p>Deadline: " + opp.getDeadline() + "</p>"
                + "<p>Please review and ensure the listing is up to date.</p>";
            emailService.sendEmail(email, subject, body);
            log.info("Deadline reminder sent for opportunity {} ({} days)", opp.getId(), days);
        } catch (Exception e) {
            log.warn("Failed to send deadline reminder for opportunity {}: {}", opp.getId(), e.getMessage());
        }
    }

    public List<DeadlineReminder> getRemindersForOpportunity(Long opportunityId) {
        return reminderRepository.findByOpportunityId(opportunityId);
    }

    private List<Integer> getReminderDays() {
        String configValue = configRepository.findByConfigKey("deadline.reminder.days")
            .map(c -> c.getConfigValue())
            .orElse("30,14,7,3,1");
        return Arrays.stream(configValue.split(","))
            .map(String::trim)
            .map(Integer::parseInt)
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
