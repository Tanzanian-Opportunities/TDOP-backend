package tdop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.Application;
import tdop.entity.Opportunity;
import tdop.entity.OrganizationProfile;
import tdop.entity.User;
import tdop.entity.enums.ApplicationStatus;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.ApplicationRepository;
import tdop.repository.ApplicationStatusHistoryRepository;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private ApplicationStatusHistoryRepository statusHistoryRepository;
    @Mock private OpportunityRepository opportunityRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CandidateService candidateService;

    private User orgUser;
    private User otherUser;
    private Opportunity opportunity;
    private Application application;

    @BeforeEach
    void setUp() {
        orgUser = User.builder().id(1L).fullName("Org User").email("org@test.com").build();
        otherUser = User.builder().id(2L).fullName("Other").email("other@test.com").build();
        OrganizationProfile orgProfile = new OrganizationProfile();
        orgProfile.setUser(orgUser);
        opportunity = Opportunity.builder().id(100L).createdBy(orgProfile).build();
        application = Application.builder().id(1000L).opportunity(opportunity).applicant(otherUser).status(ApplicationStatus.SUBMITTED).build();
    }

    @Test
    void testGetApplicantsOwner() {
        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(opportunity));
        when(applicationRepository.findByOpportunityId(100L)).thenReturn(List.of(application));

        List<Application> result = candidateService.getApplicantsForOpportunity(100L, 1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetApplicantsNotOwner() {
        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(opportunity));

        assertThrows(ForbiddenException.class, () ->
            candidateService.getApplicantsForOpportunity(100L, 999L));
    }

    @Test
    void testGetApplicantsOpportunityNotFound() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            candidateService.getApplicantsForOpportunity(999L, 1L));
    }

    @Test
    void testShortlistApplication() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any())).thenReturn(application);

        Application result = candidateService.shortlistApplication(1000L, 1L);
        assertEquals(ApplicationStatus.SHORTLISTED, result.getStatus());
        assertTrue(result.isShortlisted());
    }

    @Test
    void testRejectApplication() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any())).thenReturn(application);

        Application result = candidateService.rejectApplication(1000L, 1L, "Not qualified");
        assertEquals(ApplicationStatus.REJECTED, result.getStatus());
    }

    @Test
    void testShortlistNotOwner() {
        when(applicationRepository.findById(1000L)).thenReturn(Optional.of(application));

        assertThrows(ForbiddenException.class, () ->
            candidateService.shortlistApplication(1000L, 999L));
    }
}
