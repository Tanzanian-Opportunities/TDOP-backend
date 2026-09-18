package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.audit.AuditLogService;
import tdop.entity.Application;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.NotificationType;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.NotificationService;
import tdop.notification.email.EmailService;
import tdop.repository.ApplicationRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
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
        log.info("Application created: user={} opportunity={}", applicantId, oppId);

        opp.setApplicationCount(opp.getApplicationCount() + 1);
        opportunityRepository.save(opp);

        auditLogService.logAction("APPLY", "Application", saved.getId(), applicantId,
            null, opp.getTitle(), null);

        // Notify org owner about new application
        try {
            if (opp.getCreatedBy() != null && opp.getCreatedBy().getUser() != null) {
                Long orgUserId = opp.getCreatedBy().getUser().getId();
                notificationService.createNotification(orgUserId,
                    "New Application Received",
                    applicant.getFullName() + " applied to '" + opp.getTitle() + "'",
                    NotificationType.APPLICATION);
            }
        } catch (Exception e) {
            log.warn("Failed to send new application notification for opportunity id={}: {}", oppId, e.getMessage());
        }

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
        app.setStatus(status);
        Application saved = applicationRepository.save(app);

        auditLogService.logAction("UPDATE_APPLICATION_STATUS", "Application", id,
            app.getApplicant().getId(), oldStatus.name(), status.name(), null);

        // Notify applicant of status change
        try {
            String statusLabel = status.name().replace("_", " ").toLowerCase();
            notificationService.createNotification(app.getApplicant().getId(),
                "Application Status Updated",
                "Your application to '" + app.getOpportunity().getTitle() + "' is now: " + statusLabel,
                NotificationType.APPLICATION);
        } catch (Exception e) {
            log.warn("Failed to send status update notification for application id={}: {}", id, e.getMessage());
        }

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
