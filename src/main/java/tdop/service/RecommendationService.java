package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Opportunity;
import tdop.entity.SavedOpportunity;
import tdop.entity.SeekerProfile;
import tdop.entity.Skill;
import tdop.entity.enums.OpportunityStatus;
import tdop.repository.OpportunityRepository;
import tdop.repository.SavedOpportunityRepository;
import tdop.repository.SeekerProfileRepository;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecommendationService {

    private final OpportunityRepository opportunityRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final SavedOpportunityRepository savedOpportunityRepository;
    private final OpportunityService opportunityService;

    public List<OpportunityResponse> getRecommendations(Long userId, int limit) {
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId).orElse(null);
        List<Opportunity> published = opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED);
        if (published.isEmpty()) return Collections.emptyList();

        Set<Long> alreadyApplied = savedOpportunityRepository.findByUserId(userId).stream()
            .map(s -> s.getOpportunity().getId())
            .collect(Collectors.toSet());

        Map<Long, Double> scores = new HashMap<>();
        for (Opportunity opp : published) {
            if (alreadyApplied.contains(opp.getId())) continue;
            double score = calculateMatchScore(profile, opp);
            if (score > 0) scores.put(opp.getId(), score);
        }

        return scores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> opportunityRepository.findById(e.getKey()).orElse(null))
            .filter(Objects::nonNull)
            .map(opportunityService::toResponse)
            .collect(Collectors.toList());
    }

    private double calculateMatchScore(SeekerProfile profile, Opportunity opp) {
        double score = 0;
        if (profile == null) return score;

        if (opp.getCategory() != null && profile.getInterests() != null) {
            for (var interest : profile.getInterests()) {
                if (interest.getCategory() != null &&
                    interest.getCategory().equalsIgnoreCase(opp.getCategory())) {
                    score += 3.0;
                    break;
                }
            }
        }

        if (opp.getTags() != null && profile.getSkills() != null) {
            Set<String> oppTags = Arrays.stream(opp.getTags().split(","))
                .map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());
            for (Skill skill : profile.getSkills()) {
                if (skill.getName() != null && oppTags.contains(skill.getName().toLowerCase())) {
                    score += 2.0;
                }
            }
        }

        if (opp.getLocation() != null && profile.getLocation() != null &&
            opp.getLocation().equalsIgnoreCase(profile.getLocation())) {
            score += 1.5;
        }

        if (opp.getExperienceLevel() != null && profile.getExperiences() != null &&
            !profile.getExperiences().isEmpty()) {
            score += 0.5;
        }

        if (opp.getEducationLevel() != null && profile.getEducation() != null &&
            !profile.getEducation().isEmpty()) {
            score += 0.5;
        }

        return score;
    }

    public List<OpportunityResponse> getSimilarOpportunities(Long opportunityId, int limit) {
        Opportunity opp = opportunityRepository.findById(opportunityId).orElse(null);
        if (opp == null) return Collections.emptyList();

        List<Opportunity> published = opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED);
        return published.stream()
            .filter(o -> !o.getId().equals(opportunityId))
            .map(o -> Map.entry(o, calculateSimilarity(opp, o)))
            .filter(e -> e.getValue() > 0)
            .sorted(Map.Entry.<Opportunity, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> opportunityService.toResponse(e.getKey()))
            .collect(Collectors.toList());
    }

    private double calculateSimilarity(Opportunity a, Opportunity b) {
        double score = 0;
        if (a.getCategory() != null && a.getCategory().equalsIgnoreCase(b.getCategory())) score += 3;
        if (a.getType() != null && a.getType() == b.getType()) score += 2;
        if (a.getLocation() != null && a.getLocation().equalsIgnoreCase(b.getLocation())) score += 1.5;
        if (a.getWorkMode() != null && a.getWorkMode().equalsIgnoreCase(b.getWorkMode())) score += 1;
        return score;
    }
}
