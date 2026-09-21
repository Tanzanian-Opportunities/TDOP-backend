package tdop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.Opportunity;
import tdop.entity.User;
import tdop.entity.enums.OpportunityType;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private User testUser;
    private Opportunity testOpp;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .fullName("Test User")
            .skills("Java,Spring Boot")
            .interests("Technology,Education")
            .location("Dar es Salaam")
            .build();

        testOpp = Opportunity.builder()
            .id(100L)
            .title("Java Developer Position")
            .description("Looking for experienced Java developer with Spring Boot skills")
            .location("Dar es Salaam")
            .type(OpportunityType.JOB)
            .deadline(LocalDateTime.now().plusDays(30))
            .build();
    }

    @Test
    void testGetRecommendations() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(opportunityRepository.findAll()).thenReturn(List.of(testOpp));

        List<Opportunity> result = recommendationService.getRecommendations(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(opportunityRepository).findAll();
    }

    @Test
    void testGetRecommendationsUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        List<Opportunity> result = recommendationService.getRecommendations(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSimilarOpportunities() {
        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(testOpp));
        when(opportunityRepository.findAll()).thenReturn(List.of(testOpp));

        List<Opportunity> result = recommendationService.getSimilarOpportunities(100L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetSimilarOpportunitiesNotFound() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        List<Opportunity> result = recommendationService.getSimilarOpportunities(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
