package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Application;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
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
        log.info("Application created: user={} opportunity={}", applicantId, oppId);
        return applicationRepository.save(app);
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
        app.setStatus(status);
        return applicationRepository.save(app);
    }

    public Application withdrawApplication(Long id, Long userId) {
        Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        if (!app.getApplicant().getId().equals(userId)) {
            throw new ForbiddenException("You can only withdraw your own applications");
        }
        if (app.getStatus() == ApplicationStatus.REJECTED || app.getStatus() == ApplicationStatus.OFFER || app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("Cannot withdraw an application with status: " + app.getStatus());
        }
        app.setStatus(ApplicationStatus.WITHDRAWN);
        log.info("Application withdrawn: id={} user={}", id, userId);
        return applicationRepository.save(app);
    }
}
