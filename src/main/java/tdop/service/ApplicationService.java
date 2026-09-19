package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.audit.AuditLogService;
import tdop.entity.Application;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.email.EmailService;
import tdop.repository.ApplicationRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;

    public Application apply(Long oppId, Long applicantId, String coverLetter, String resumeUrl) {
        if (applicationRepository.findByApplicantIdAndOpportunityId(applicantId, oppId).isPresent()) {
            throw new BadRequestException("You have already applied to this opportunity");
        }
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        User applicant = userRepository.findById(applicantId)
            .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
        Application app = Application.builder()
            .opportunity(opp)
            .applicant(applicant)
            .status(ApplicationStatus.APPLIED)
            .coverLetter(coverLetter)
            .resumeUrl(resumeUrl)
            .build();
        Application saved = applicationRepository.save(app);

        opp.setApplicationCount(opp.getApplicationCount() + 1);
        opportunityRepository.save(opp);

        auditLogService.logAction("APPLY", "Application", saved.getId(), applicantId,
            null, opp.getTitle(), null);

        if (opp.getCreatedBy() != null && opp.getCreatedBy().getUser() != null) {
            try {
                emailService.sendApplicationNotification(
                    opp.getCreatedBy().getUser().getEmail(),
                    opp.getTitle(),
                    applicant.getFullName());
            } catch (Exception e) {
                log.warn("Could not send application notification email: {}", e.getMessage());
            }
        }

        log.info("Application created: user={} opportunity={}", applicantId, oppId);
        return saved;
    }

    public List<Application> getMyApplications(Long userId) {
        return applicationRepository.findByApplicantId(userId);
    }

    public List<Application> getApplicants(Long oppId) {
        return applicationRepository.findByOpportunityId(oppId);
    }

    public Application updateStatus(Long id, ApplicationStatus status) {
        Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        ApplicationStatus oldStatus = app.getStatus();
        LifecycleValidator.validateApplicationTransition(oldStatus, status);
        app.setStatus(status);
        Application saved = applicationRepository.save(app);

        auditLogService.logAction("UPDATE_APPLICATION_STATUS", "Application", id,
            app.getApplicant().getId(), oldStatus.name(), status.name(), null);

        log.info("Application status updated: id={} {} -> {}", id, oldStatus, status);
        return saved;
    }

    public Application withdrawApplication(Long id, Long userId) {
        Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (!app.getApplicant().getId().equals(userId)) {
            throw new ForbiddenException("You can only withdraw your own applications");
        }
        if (app.getStatus() == ApplicationStatus.REJECTED || app.getStatus() == ApplicationStatus.ACCEPTED || app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("Cannot withdraw an application with status: " + app.getStatus());
        }
        ApplicationStatus oldStatus = app.getStatus();
        app.setStatus(ApplicationStatus.WITHDRAWN);
        Application saved = applicationRepository.save(app);

        auditLogService.logAction("WITHDRAW_APPLICATION", "Application", id, userId,
            oldStatus.name(), "WITHDRAWN", null);

        log.info("Application withdrawn: id={} user={}", id, userId);
        return saved;
    }
}
