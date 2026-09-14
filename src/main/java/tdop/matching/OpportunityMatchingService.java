package tdop.matching;

import org.springframework.stereotype.Service;
import tdop.entity.Opportunity;
import tdop.entity.Skill;
import tdop.entity.SeekerProfile;
import tdop.entity.Education;
import tdop.entity.Interest;
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

        Set<String> interestCategories = new HashSet<>();
        profile.getInterests().forEach(i -> interestCategories.add(i.getCategory().toLowerCase()));

        String educationLevel = "";
        if (profile.getEducation() != null && !profile.getEducation().isEmpty()) {
            Education latest = profile.getEducation().get(0);
            educationLevel = latest.getDegree() != null ? latest.getDegree().toLowerCase() : "";
        }

        List<Opportunity> published = opportunityRepository.findPublished();
        for (Opportunity opp : published) {
            Map<String, Object> result = calculateMatchWithReasons(opp, skillNames, interestCategories, educationLevel);
            int score = (int) result.get("score");
            if (score > 0) {
                Map<String, Object> match = new HashMap<>();
                match.put("opportunity", opp);
                match.put("score", score);
                match.put("reasons", result.get("reasons"));
                match.put("matchPercentage", Math.min(score, 100));
                matches.add(match);
            }
        }
        matches.sort((a, b) -> ((int) b.get("score")) - ((int) a.get("score")));
        return matches;
    }

    private Map<String, Object> calculateMatchWithReasons(Opportunity opp, Set<String> skillNames,
                                                           Set<String> interestCategories, String educationLevel) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        if (opp.getTags() != null) {
            for (String tag : opp.getTags().split(",")) {
                String trimmed = tag.trim().toLowerCase();
                if (skillNames.contains(trimmed)) {
                    score += 10;
                    reasons.add("Skill match: " + tag.trim());
                }
            }
        }

        if (opp.getCategory() != null && interestCategories.contains(opp.getCategory().toLowerCase())) {
            score += 5;
            reasons.add("Interest match: " + opp.getCategory());
        }

        if (opp.getRequiredDocuments() != null && !opp.getRequiredDocuments().isEmpty()) {
            if (educationLevel.contains("bachelor") || educationLevel.contains("degree")) {
                score += 3;
                reasons.add("Education level meets requirements");
            }
        }

        if (opp.getLocation() != null && !opp.getLocation().isEmpty()) {
            score += 1;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("reasons", reasons);
        return result;
    }
}
