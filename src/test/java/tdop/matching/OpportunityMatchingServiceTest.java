package tdop.matching;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tdop.entity.Opportunity;
import tdop.entity.Skill;
import tdop.entity.SeekerProfile;
import tdop.entity.User;
import tdop.entity.enums.SkillLevel;
import tdop.repository.OpportunityRepository;
import tdop.repository.SkillRepository;
import tdop.repository.SeekerProfileRepository;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class OpportunityMatchingServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SeekerProfileRepository seekerProfileRepository;

    @InjectMocks
    private OpportunityMatchingService matchingService;

    private SeekerProfile profile;
    private User user;
    private Skill skill1;
    private Skill skill2;
    private Opportunity opportunity;
    private List<Map<String, Object>> matches;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("seeker@example.com").build();
        profile = SeekerProfile.builder().id(1L).user(user).build();

        skill1 = Skill.builder().id(1L).name("Java").level(SkillLevel.ADVANCED).user(user).build();
        skill2 = Skill.builder().id(2L).name("Spring").level(SkillLevel.INTERMEDIATE).user(user).build();

        opportunity = Opportunity.builder().id(1L).title("Java Developer")
            .tags("Java,Spring,React").status(tdop.entity.enums.OpportunityStatus.PUBLISHED).build();

        matches = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        profile = null;
        user = null;
        skill1 = null;
        skill2 = null;
        opportunity = null;
        matches = null;
    }

    @Test
    void testMatchOpportunities() {
        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByUserId(1L)).thenReturn(List.of(skill1, skill2));
        when(opportunityRepository.findPublished()).thenReturn(List.of(opportunity));

        List<Map<String, Object>> result = matchingService.matchOpportunities(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(seekerProfileRepository).findByUserId(1L);
        verify(skillRepository).findByUserId(1L);
        verify(opportunityRepository).findPublished();
    }

    @Test
    void testMatchOpportunitiesProfileNotFound() {
        when(seekerProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchingService.matchOpportunities(999L);
        });
        assertEquals("Profile not found", exception.getMessage());
    }

    @Test
    void testMatchOpportunitiesNoMatchingSkills() {
        Skill skillPython = Skill.builder().id(3L).name("Python").level(SkillLevel.ADVANCED).user(user).build();
        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByUserId(1L)).thenReturn(List.of(skillPython));
        when(opportunityRepository.findPublished()).thenReturn(List.of(opportunity));

        List<Map<String, Object>> result = matchingService.matchOpportunities(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateMatchScore() {
        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByUserId(1L)).thenReturn(List.of(skill1, skill2));
        when(opportunityRepository.findPublished()).thenReturn(List.of(opportunity));

        List<Map<String, Object>> result = matchingService.matchOpportunities(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        Map<String, Object> match = result.get(0);
        int score = (int) match.get("score");
        assertTrue(score > 0);
        assertEquals(opportunity, match.get("opportunity"));
    }

    @Test
    void testMatchOpportunitiesSortedByScore() {
        Skill skillJava = Skill.builder().id(1L).name("Java").level(SkillLevel.ADVANCED).user(user).build();
        Skill skillPython = Skill.builder().id(2L).name("Python").level(SkillLevel.INTERMEDIATE).user(user).build();

        Opportunity oppJava = Opportunity.builder().id(1L).title("Java Developer")
            .tags("Java,Spring").status(tdop.entity.enums.OpportunityStatus.PUBLISHED).build();
        Opportunity oppPython = Opportunity.builder().id(2L).title("Python Developer")
            .tags("Python,Django").status(tdop.entity.enums.OpportunityStatus.PUBLISHED).build();

        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByUserId(1L)).thenReturn(List.of(skillJava));
        when(opportunityRepository.findPublished()).thenReturn(List.of(oppJava, oppPython));

        List<Map<String, Object>> result = matchingService.matchOpportunities(1L);

        assertNotNull(result);
        if (result.size() >= 2) {
            int score1 = (int) result.get(0).get("score");
            int score2 = (int) result.get(1).get("score");
            assertTrue(score1 >= score2);
        }
    }

    @Test
    void testMatchOpportunitiesNoPublishedOpportunities() {
        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByUserId(1L)).thenReturn(List.of(skill1));
        when(opportunityRepository.findPublished()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = matchingService.matchOpportunities(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMatchOpportunitiesNullSkills() {
        when(seekerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(opportunityRepository.findPublished()).thenReturn(List.of(opportunity));

        List<Map<String, Object>> result = matchingService.matchOpportunities(1L);

        assertTrue(result.isEmpty());
    }
}
