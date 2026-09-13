package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Application;
import tdop.entity.ApplicationStatusHistory;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.entity.enums.ApplicationStatus;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.ApplicationRepository;
import tdop.repository.ApplicationStatusHistoryRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CandidateService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public List<Application> getApplicantsForOpportunity(Long opportunityId, Long orgUserId) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (opp.getCreatedBy() == null || !opp.getCreatedBy().getUser().getId().equals(orgUserId)) {
            throw new ForbiddenException("Not authorized to view applicants for this opportunity");
        }
        return applicationRepository.findByOpportunityId(opportunityId);
    }

    public List<Application> searchApplicants(Long opportunityId, String keyword, Long orgUserId) {
        List<Application> applicants = getApplicantsForOpportunity(opportunityId, orgUserId);
        if (keyword == null || keyword.isBlank()) return applicants;
        String lowerKeyword = keyword.toLowerCase();
        return applicants.stream()
            .filter(a -> a.getApplicant().getFullName().toLowerCase().contains(lowerKeyword)
                || a.getApplicant().getEmail().toLowerCase().contains(lowerKeyword))
            .collect(Collectors.toList());
    }

    public Application shortlistApplication(Long applicationId, Long orgUserId) {
        Application app = getAndValidateOrgAccess(applicationId, orgUserId);
        app.setShortlisted(true);
        app.setShortlistedAt(LocalDateTime.now());
        app.setStatus(ApplicationStatus.SHORTLISTED);
        addStatusHistory(app, ApplicationStatus.SHORTLISTED, orgUserId, "Shortlisted");
        return applicationRepository.save(app);
    }

    public Application rejectApplication(Long applicationId, Long orgUserId, String reason) {
        Application app = getAndValidateOrgAccess(applicationId, orgUserId);
        app.setStatus(ApplicationStatus.REJECTED);
        addStatusHistory(app, ApplicationStatus.REJECTED, orgUserId, reason);
        return applicationRepository.save(app);
    }

    public Application updateApplicationStatus(Long applicationId, Long orgUserId, ApplicationStatus newStatus, String notes) {
        Application app = getAndValidateOrgAccess(applicationId, orgUserId);
        app.setStatus(newStatus);
        if (newStatus == ApplicationStatus.INTERVIEW) {
            app.setReviewedBy(userRepository.findById(orgUserId).orElse(null));
            app.setReviewedAt(LocalDateTime.now());
        }
        addStatusHistory(app, newStatus, orgUserId, notes);
        return applicationRepository.save(app);
    }

    public Application addInternalNote(Long applicationId, Long orgUserId, String notes) {
        Application app = getAndValidateOrgAccess(applicationId, orgUserId);
        app.setInternalNotes(notes);
        return applicationRepository.save(app);
    }

    public List<ApplicationStatusHistory> getApplicationTimeline(Long applicationId) {
        return statusHistoryRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId);
    }

    public long countByStatus(Long opportunityId, ApplicationStatus status) {
        return applicationRepository.countByOpportunityIdAndStatus(opportunityId, status);
    }

    public long countShortlisted(Long opportunityId) {
        return applicationRepository.countByOpportunityIdAndShortlisted(opportunityId, true);
    }

    private Application getAndValidateOrgAccess(Long applicationId, Long orgUserId) {
        Application app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        Opportunity opp = app.getOpportunity();
        if (opp.getCreatedBy() == null || !opp.getCreatedBy().getUser().getId().equals(orgUserId)) {
            throw new ForbiddenException("Not authorized to manage this application");
        }
        return app;
    }

    private void addStatusHistory(Application app, ApplicationStatus newStatus, Long userId, String notes) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
            .application(app)
            .oldStatus(app.getStatus() != null ? app.getStatus().name() : null)
            .newStatus(newStatus.name())
            .changedBy(userRepository.findById(userId).orElse(null))
            .notes(notes)
            .build();
        statusHistoryRepository.save(history);
    }
}
