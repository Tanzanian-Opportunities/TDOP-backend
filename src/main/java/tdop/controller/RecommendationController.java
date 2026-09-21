package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.dto.response.OpportunityResponse;
import tdop.repository.UserRepository;
import tdop.service.RecommendationService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getRecommendations(
            Authentication auth,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(recommendationService.getRecommendations(userId, limit));
    }

    @GetMapping("/similar/{opportunityId}")
    public ResponseEntity<List<OpportunityResponse>> getSimilar(
            @PathVariable Long opportunityId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(recommendationService.getSimilarOpportunities(opportunityId, limit));
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
    }
}
