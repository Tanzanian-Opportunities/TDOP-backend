package tdop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Interest;
import tdop.entity.Opportunity;
import tdop.entity.SavedOpportunity;
import tdop.entity.SeekerProfile;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.enums.OpportunityType;
import tdop.repository.OpportunityRepository;
import tdop.repository.SavedOpportunityRepository;
import tdop.repository.SeekerProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private SeekerProfileRepository seekerProfileRepository;

    @Mock
    private SavedOpportunityRepository savedOpportunityRepository;

    @Mock
    private OpportunityService opportunityService;

    @InjectMocks
    private RecommendationService recommendationService;

    private Opportunity testOpp;

    @BeforeEach
    void setUp() {
        testOpp = Opportunity.builder()
            .id(100L)
            .title("Java Developer Position")
            .description("Looking for experienced Java developer with Spring Boot skills")
            .location("Dar es Salaam")
            .category("Technology")
            .type(OpportunityType.FULL_TIME)
            .tags("java,spring boot")
            .deadline(LocalDateTime.now().plusDays(30))
            .build();
    }

    private SeekerProfile profileWithMatchingInterests() {
        SeekerProfile profile = SeekerProfile.builder()
            .location("Dar es Salaam")
            .build();
        profile.setInterests(List.of(Interest.builder().category("Technology").build()));
        return profile;
    }

    @Test
    void testGetRecommendations() {
        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profileWithMatchingInterests()));
        when(opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED)).thenReturn(List.of(testOpp));
        when(savedOpportunityRepository.findByUserId(1L)).thenReturn(List.of());
        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(testOpp));
        when(opportunityService.toResponse(testOpp))
            .thenReturn(OpportunityResponse.builder().id(100L).title("Java Developer Position").build());

        List<OpportunityResponse> result = recommendationService.getRecommendations(1L, 5);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(100L, result.get(0).getId().longValue());
        verify(opportunityRepository).findByStatus(OpportunityStatus.PUBLISHED);
    }

    @Test
    void testGetRecommendationsNoMatchingProfile() {
        when(seekerProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());
        when(opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED)).thenReturn(List.of());

        List<OpportunityResponse> result = recommendationService.getRecommendations(999L, 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSimilarOpportunities() {
        Opportunity similar = Opportunity.builder()
            .id(101L)
            .title("Spring Boot Developer")
            .location("Dar es Salaam")
            .category("Technology")
            .type(OpportunityType.FULL_TIME)
            .build();

        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(testOpp));
        when(opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED)).thenReturn(List.of(testOpp, similar));
        when(opportunityService.toResponse(similar))
            .thenReturn(OpportunityResponse.builder().id(101L).title("Spring Boot Developer").build());

        List<OpportunityResponse> result = recommendationService.getSimilarOpportunities(100L, 5);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(101L, result.get(0).getId().longValue());
    }

    @Test
    void testGetSimilarOpportunitiesNotFound() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        List<OpportunityResponse> result = recommendationService.getSimilarOpportunities(999L, 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSimilarOpportunitiesSkipsSelf() {
        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(testOpp));
        when(opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED)).thenReturn(List.of(testOpp));

        List<OpportunityResponse> result = recommendationService.getSimilarOpportunities(100L, 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}