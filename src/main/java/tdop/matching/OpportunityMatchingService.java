package tdop.matching;

import org.springframework.stereotype.Service;
import tdop.entity.Opportunity;
import tdop.entity.Skill;
import tdop.entity.SeekerProfile;
import tdop.repository.OpportunityRepository;
import tdop.repository.SkillRepository;
import tdop.repository.SeekerProfileRepository;
import java.util.*;

@Service
public class OpportunityMatchingService {

    private final OpportunityRepository opportunityRepository;
    private final SkillRepository skillRepository;
    private final SeekerProfileRepository seekerProfileRepository;

    public OpportunityMatchingService(OpportunityRepository opportunityRepository,
                                       SkillRepository skillRepository,
                                       SeekerProfileRepository seekerProfileRepository) {
        this.opportunityRepository = opportunityRepository;
        this.skillRepository = skillRepository;
        this.seekerProfileRepository = seekerProfileRepository;
    }

    public List<Map<String, Object>> matchOpportunities(Long seekerId) {
        List<Map<String, Object>> matches = new ArrayList<>();
        SeekerProfile profile = seekerProfileRepository.findByUserId(seekerId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        List<Skill> skills = skillRepository.findByUserId(seekerId);
        Set<String> skillNames = new HashSet<>();
        skills.forEach(s -> skillNames.add(s.getName().toLowerCase()));

        List<Opportunity> published = opportunityRepository.findPublished();
        for (Opportunity opp : published) {
            int score = calculateMatchScore(opp, skillNames);
            if (score > 0) {
                Map<String, Object> match = new HashMap<>();
                match.put("opportunity", opp);
                match.put("score", score);
                matches.add(match);
            }
        }
        matches.sort((a, b) -> ((int) b.get("score")) - ((int) a.get("score")));
        return matches;
    }

    private int calculateMatchScore(Opportunity opp, Set<String> skillNames) {
        int score = 0;
        if (opp.getTags() != null) {
            for (String tag : opp.getTags().split(",")) {
                if (skillNames.contains(tag.trim().toLowerCase())) score += 10;
            }
        }
        return score;
    }
}
