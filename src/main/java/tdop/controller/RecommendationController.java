package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tdop.matching.OpportunityMatchingService;
import tdop.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final OpportunityMatchingService matchingService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getRecommendations() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(matchingService.matchOpportunities(userId));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserIdByEmail(email);
    }
}
