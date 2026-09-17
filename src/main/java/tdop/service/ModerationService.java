package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.ModerationAction;
import tdop.entity.Opportunity;
import tdop.entity.enums.NotificationType;
import tdop.entity.enums.OpportunityStatus;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.NotificationService;
import tdop.repository.ModerationActionRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ModerationActionRepository moderationActionRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final Set<OpportunityStatus> MODERATABLE_STATUSES =
        Set.of(OpportunityStatus.SUBMITTED, OpportunityStatus.UNDER_REVIEW, OpportunityStatus.CLOSING_SOON);

    public List<Opportunity> getPendingModerationQueue() {
        return opportunityRepository.findPendingModeration();
    }

    public ModerationAction approve(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = getAndValidateModeratable(opportunityId);
        transitionAndNotify(opp, OpportunityStatus.APPROVED, moderatorId, reason, "APPROVE");
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId).stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Moderation action not found after save"));
    }

    public ModerationAction reject(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = getAndValidateModeratable(opportunityId);
        transitionAndNotify(opp, OpportunityStatus.REJECTED, moderatorId, reason, "REJECT");
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId).stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Moderation action not found after save"));
    }

    public ModerationAction suspend(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = getAndValidateModeratable(opportunityId);
        transitionAndNotify(opp, OpportunityStatus.SUSPENDED, moderatorId, reason, "SUSPEND");
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId).stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Moderation action not found after save"));
    }

    public ModerationAction archive(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.ARCHIVED);
        opp.setModerated(true);
        opp.setModeratedAt(LocalDateTime.now());
        opportunityRepository.save(opp);
        logOpportunity(opp, moderatorId, "ARCHIVE", reason);
        notifyOrgOwner(opp, "Opportunity Archived",
            "Your opportunity '" + opp.getTitle() + "' has been archived.",
            NotificationType.MODERATION);
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId).stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Moderation action not found after save"));
    }

    public ModerationAction requestInformation(Long opportunityId, Long moderatorId, String details) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (opp.getStatus() != OpportunityStatus.SUBMITTED && opp.getStatus() != OpportunityStatus.UNDER_REVIEW) {
            throw new BadRequestException("Can only request information for submitted/under-review opportunities");
        }
        opp.setStatus(OpportunityStatus.UNDER_REVIEW);
        opportunityRepository.save(opp);
        logOpportunity(opp, moderatorId, "REQUEST_INFORMATION", details);
        notifyOrgOwner(opp, "Information Requested",
            "A moderator has requested more information for '" + opp.getTitle() + "': " + details,
            NotificationType.MODERATION);
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId).stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Moderation action not found after save"));
    }

    public List<ModerationAction> getHistory(Long opportunityId) {
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId);
    }

    public List<ModerationAction> getModeratorActions(Long moderatorId) {
        return moderationActionRepository.findByModeratorId(moderatorId);
    }

    private Opportunity getAndValidateModeratable(Long opportunityId) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (!MODERATABLE_STATUSES.contains(opp.getStatus())) {
            throw new BadRequestException(
                "Cannot moderate opportunity in status '" + opp.getStatus() + "'. Must be: " + MODERATABLE_STATUSES);
        }
        return opp;
    }

    private void transitionAndNotify(Opportunity opp, OpportunityStatus newStatus, Long moderatorId, String reason, String actionName) {
        OpportunityStatus oldStatus = opp.getStatus();
        opp.setStatus(newStatus);
        opp.setModerated(true);
        opp.setModeratedAt(LocalDateTime.now());
        opportunityRepository.save(opp);

        logOpportunity(opp, moderatorId, actionName, reason);

        NotificationType type = newStatus == OpportunityStatus.APPROVED ? NotificationType.VERIFICATION : NotificationType.MODERATION;
        String title = "Opportunity " + actionName.charAt(0) + actionName.substring(1).toLowerCase();
        String message = "Your opportunity '" + opp.getTitle() + "' has been " + actionName.toLowerCase() + "."
            + (reason != null ? " Reason: " + reason : "");
        notifyOrgOwner(opp, title, message, type);
    }

    private void logOpportunity(Opportunity opp, Long moderatorId, String action, String reason) {
        ModerationAction actionEntity = ModerationAction.builder()
            .opportunity(opp)
            .moderator(userRepository.findById(moderatorId).orElse(null))
            .action(action)
            .reason(reason)
            .build();
        moderationActionRepository.save(actionEntity);
    }

    private void notifyOrgOwner(Opportunity opp, String title, String message, NotificationType type) {
        try {
            if (opp.getCreatedBy() != null && opp.getCreatedBy().getUser() != null) {
                Long orgUserId = opp.getCreatedBy().getUser().getId();
                notificationService.createNotification(orgUserId, title, message, type);
            }
        } catch (Exception e) {
            log.error("Failed to send moderation notification for opportunity id={}: {}", opp.getId(), e.getMessage());
        }
    }
}
