package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Application;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.NotificationType;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.NotificationService;
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
        app.setStatus(ApplicationStatus.WITHDRAWN);
        log.info("Application withdrawn: id={} user={}", id, userId);
        return applicationRepository.save(app);
    }
}
