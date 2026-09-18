package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.audit.AuditLogService;
import tdop.entity.SavedOpportunity;
import tdop.entity.User;
import tdop.entity.Opportunity;
import tdop.repository.SavedOpportunityRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import tdop.exception.ResourceNotFoundException;
import java.util.List;

@Service
@Transactional
public class SavedOpportunityService {

    private final SavedOpportunityRepository savedRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public SavedOpportunityService(SavedOpportunityRepository savedRepository,
                                     OpportunityRepository opportunityRepository,
                                     UserRepository userRepository,
                                     AuditLogService auditLogService) {
        this.savedRepository = savedRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public SavedOpportunity saveOpportunity(Long userId, Long oppId) {
        if (savedRepository.findByUserIdAndOpportunityId(userId, oppId).isPresent()) {
            throw new RuntimeException("Already saved");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        SavedOpportunity saved = savedRepository.save(SavedOpportunity.builder().user(user).opportunity(opp).build());

        opp.setSaveCount(opp.getSaveCount() + 1);
        opportunityRepository.save(opp);

        auditLogService.logAction("SAVE_OPPORTUNITY", "SavedOpportunity", saved.getId(), userId,
            null, opp.getTitle(), null);

        return saved;
    }

    public List<SavedOpportunity> getSavedOpportunities(Long userId) {
        return savedRepository.findByUserId(userId);
    }

    public void unsaveOpportunity(Long userId, Long oppId) {
        savedRepository.deleteByUserIdAndOpportunityId(userId, oppId);
        Opportunity opp = opportunityRepository.findById(oppId).orElse(null);
        if (opp != null && opp.getSaveCount() > 0) {
            opp.setSaveCount(opp.getSaveCount() - 1);
            opportunityRepository.save(opp);
        }
        auditLogService.logAction("UNSAVE_OPPORTUNITY", "SavedOpportunity", null, userId,
            null, String.valueOf(oppId), null);
    }
}
