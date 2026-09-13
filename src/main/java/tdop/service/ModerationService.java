package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.ModerationAction;
import tdop.entity.Opportunity;
import tdop.entity.enums.OpportunityStatus;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.ModerationActionRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ModerationService {

    private final ModerationActionRepository moderationActionRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public List<Opportunity> getPendingModerationQueue() {
        return opportunityRepository.findPendingModeration();
    }

    public ModerationAction approve(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.APPROVED);
        opp.setModerated(true);
        opp.setModeratedAt(LocalDateTime.now());
        opportunityRepository.save(opp);

        ModerationAction action = ModerationAction.builder()
            .opportunity(opp)
            .moderator(userRepository.findById(moderatorId).orElse(null))
            .action("APPROVE")
            .reason(reason)
            .build();
        return moderationActionRepository.save(action);
    }

    public ModerationAction reject(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.REJECTED);
        opp.setModerated(true);
        opp.setModeratedAt(LocalDateTime.now());
        opportunityRepository.save(opp);

        ModerationAction action = ModerationAction.builder()
            .opportunity(opp)
            .moderator(userRepository.findById(moderatorId).orElse(null))
            .action("REJECT")
            .reason(reason)
            .build();
        return moderationActionRepository.save(action);
    }

    public ModerationAction suspend(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.SUSPENDED);
        opp.setModerated(true);
        opp.setModeratedAt(LocalDateTime.now());
        opportunityRepository.save(opp);

        ModerationAction action = ModerationAction.builder()
            .opportunity(opp)
            .moderator(userRepository.findById(moderatorId).orElse(null))
            .action("SUSPEND")
            .reason(reason)
            .build();
        return moderationActionRepository.save(action);
    }

    public ModerationAction archive(Long opportunityId, Long moderatorId, String reason) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.ARCHIVED);
        opportunityRepository.save(opp);

        ModerationAction action = ModerationAction.builder()
            .opportunity(opp)
            .moderator(userRepository.findById(moderatorId).orElse(null))
            .action("ARCHIVE")
            .reason(reason)
            .build();
        return moderationActionRepository.save(action);
    }

    public ModerationAction requestInformation(Long opportunityId, Long moderatorId, String details) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.UNDER_REVIEW);
        opportunityRepository.save(opp);

        ModerationAction action = ModerationAction.builder()
            .opportunity(opp)
            .moderator(userRepository.findById(moderatorId).orElse(null))
            .action("REQUEST_INFORMATION")
            .details(details)
            .build();
        return moderationActionRepository.save(action);
    }

    public List<ModerationAction> getHistory(Long opportunityId) {
        return moderationActionRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId);
    }

    public List<ModerationAction> getModeratorActions(Long moderatorId) {
        return moderationActionRepository.findByModeratorId(moderatorId);
    }
}
