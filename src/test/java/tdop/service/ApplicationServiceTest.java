package tdop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.dto.request.ApplicationRequest;
import tdop.entity.Application;
import tdop.entity.ApplicationStatus;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.ApplicationRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private ApplicationRequest appRequest;
    private Application application;
    private Opportunity opportunity;
    private User applicant;

    @BeforeEach
    void setUp() {
        appRequest = new ApplicationRequest();
        appRequest.setCoverLetter("I am passionate about this role.");
        appRequest.setResumeUrl("http://example.com/resume.pdf");

        applicant = User.builder().id(2L).email("applicant@example.com").build();
        opportunity = Opportunity.builder().id(1L).title("Software Engineer").build();
        application = Application.builder().id(1L).applicant(applicant).opportunity(opportunity)
            .status(ApplicationStatus.APPLIED).build();
    }

    @AfterEach
    void tearDown() {
        appRequest = null;
        application = null;
        opportunity = null;
        applicant = null;
    }

    @Test
    void testApplySuccess() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(userRepository.findById(2L)).thenReturn(Optional.of(applicant));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        Application result = applicationService.apply(1L, 2L, "I am passionate", "http://example.com/resume.pdf");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ApplicationStatus.APPLIED, result.getStatus());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void testApplyOpportunityNotFound() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            applicationService.apply(999L, 2L, "cover", "resume");
        });
        assertEquals("Opportunity not found", exception.getMessage());
    }

    @Test
    void testApplyApplicantNotFound() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            applicationService.apply(1L, 999L, "cover", "resume");
        });
        assertEquals("Applicant not found", exception.getMessage());
    }

    @Test
    void testGetMyApplications() {
        when(applicationRepository.findByApplicantId(2L)).thenReturn(List.of(application));

        List<Application> result = applicationService.getMyApplications(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getApplicant().getId());
    }

    @Test
    void testUpdateStatus() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        application.setStatus(ApplicationStatus.INTERVIEW);

        Application result = applicationService.updateStatus(1L, ApplicationStatus.INTERVIEW);

        assertNotNull(result);
        assertEquals(ApplicationStatus.INTERVIEW, result.getStatus());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void testUpdateStatusNotFound() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            applicationService.updateStatus(999L, ApplicationStatus.INTERVIEW);
        });
        assertEquals("Application not found", exception.getMessage());
    }

    @Test
    void testGetApplicants() {
        when(applicationRepository.findByOpportunityId(1L)).thenReturn(List.of(application));

        List<Application> result = applicationService.getApplicants(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findByOpportunityId(1L);
    }
}
