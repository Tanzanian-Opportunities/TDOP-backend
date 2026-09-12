package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.service.OpportunityService;
import tdop.service.AnalyticsService;

@RestController
@RequestMapping("/api/v1/admin/opportunities")
@RequiredArgsConstructor
public class AdminOpportunityController {

    private final OpportunityService opportunityService;
    private final AnalyticsService analyticsService;

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        return ResponseEntity.ok("Verified");
    }

    @DeleteMapping("/{id}/moderate")
    public ResponseEntity<?> moderate(@PathVariable Long id) {
        return ResponseEntity.ok("Moderated");
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> analytics() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }
}
