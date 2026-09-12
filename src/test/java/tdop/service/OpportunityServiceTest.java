package tdop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Opportunity;
import tdop.entity.enums.OpportunityType;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.OrganizationProfile;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import tdop.repository.OrganizationProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private OrganizationProfileRepository organizationRepository;

    @InjectMocks
    private OpportunityService opportunityService;

    private OpportunityRequest request;
    private OpportunityResponse response;
    private Opportunity opportunity;
    private OrganizationProfile org;

    @BeforeEach
    void setUp() {
        request = new OpportunityRequest();
        request.setTitle("Software Engineer");
        request.setDescription("Full-time role");
        request.setLocation("Remote");
        request.setType("FULL_TIME");
        request.setCategory("Engineering");
        request.setSalaryRange("80000-120000");
        request.setTags("Java,Spring");
        request.setDeadline(LocalDateTime.now().plusDays(30));

        org = OrganizationProfile.builder().id(1L).orgName("Test Corp").build();
        opportunity = Opportunity.builder().id(1L).title("Software Engineer")
            .status(OpportunityStatus.DRAFT).createdBy(org).build();
        response = OpportunityResponse.builder().id(1L).title("Software Engineer")
            .status("DRAFT").location("Remote").type("FULL_TIME")
            .category("Engineering").salaryRange("80000-120000")
            .tags("Java,Spring").deadline(LocalDateTime.now().plusDays(30))
            .createdAt(LocalDateTime.now()).build();
    }

    @AfterEach
    void tearDown() {
        request = null;
        response = null;
        opportunity = null;
        org = null;
    }

    @Test
    void testGetAllOpportunities() {
        when(opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED)).thenReturn(List.of(opportunity));

        List<OpportunityResponse> result = opportunityService.getAllOpportunities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Software Engineer", result.get(0).getTitle());
        assertEquals("PUBLISHED", result.get(0).getStatus());
    }

    @Test
    void testSearchOpportunities() {
        when(opportunityRepository.findByTitleContainingIgnoreCase("engineer")).thenReturn(List.of(opportunity));

        List<OpportunityResponse> result = opportunityService.searchOpportunities("engineer");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(opportunityRepository).findByTitleContainingIgnoreCase("engineer");
    }

    @Test
    void testCreateOpportunity() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        OpportunityResponse result = opportunityService.createOpportunity(request, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Software Engineer", result.getTitle());
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    void testCreateOpportunityOrgNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            opportunityService.createOpportunity(request, 1L);
        });
        assertEquals("Organization not found", exception.getMessage());
    }

    @Test
    void testUpdateOpportunity() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

        OpportunityResponse result = opportunityService.updateOpportunity(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    void testPublishOpportunity() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(opportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);
        opportunity.setStatus(OpportunityStatus.PUBLISHED);

        OpportunityResponse result = opportunityService.publishOpportunity(1L);

        assertNotNull(result);
        assertEquals("PUBLISHED", result.getStatus());
    }

    @Test
    void testFilterByCategory() {
        when(opportunityRepository.findByCategoryAndStatus("Engineering", OpportunityStatus.PUBLISHED))
            .thenReturn(List.of(opportunity));

        List<OpportunityResponse> result = opportunityService.filterByCategory("Engineering");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(opportunityRepository).findByCategoryAndStatus("Engineering", OpportunityStatus.PUBLISHED);
    }

    @Test
    void testDeleteOpportunity() {
        doNothing().when(opportunityRepository).deleteById(1L);
        opportunityService.deleteOpportunity(1L);
        verify(opportunityRepository).deleteById(1L);
    }
}
