package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Application;
import tdop.entity.ApplicationStatus;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.ApplicationRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.util.List;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                                OpportunityRepository opportunityRepository,
                                UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
    }

    public Application apply(Long oppId, Long applicantId, String coverLetter, String resumeUrl) {
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        User applicant = userRepository.findById(applicantId)
            .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
        Application app = Application.builder().opportunity(opp).applicant(applicant)
            .status(ApplicationStatus.APPLIED).coverLetter(coverLetter).resumeUrl(resumeUrl).build();
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
}
