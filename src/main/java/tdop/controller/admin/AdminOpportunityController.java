package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.dto.response.OpportunityResponse;
import tdop.service.AnalyticsService;
import tdop.service.ModerationService;
import tdop.service.OpportunityLifecycleService;
import tdop.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/opportunities")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminOpportunityController {

    private final OpportunityLifecycleService opportunityLifecycleService;
    private final ModerationService moderationService;
    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> listPending() {
        return ResponseEntity.ok(opportunityLifecycleService.getPendingReview());
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<OpportunityResponse> verify(@PathVariable Long id,
                                                       @RequestParam boolean approved,
                                                       @RequestParam(required = false) String reason,
                                                       Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(opportunityLifecycleService.verifyOpportunity(id, userId, approved, reason));
    }

    @PostMapping("/{id}/moderate")
    public ResponseEntity<?> moderate(@PathVariable Long id,
                                       @RequestParam String action,
                                       @RequestParam(required = false) String reason,
                                       Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(moderationService.approve(id, userId, reason));
    }

    @DeleteMapping("/{id}/moderate")
    public ResponseEntity<?> moderateReject(@PathVariable Long id,
                                             @RequestParam(required = false) String reason,
                                             Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(moderationService.reject(id, userId, reason));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<?> suspend(@PathVariable Long id,
                                      @RequestParam(required = false) String reason,
                                      Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(moderationService.suspend(id, userId, reason));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> analytics() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/moderation-queue")
    public ResponseEntity<?> moderationQueue() {
        return ResponseEntity.ok(moderationService.getPendingModerationQueue());
    }

    private Long getUserId(Authentication auth) {
        return userService.getUserIdByEmail(auth.getName());
    }
}
