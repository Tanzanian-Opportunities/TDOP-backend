package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tdop.dto.response.DashboardResponse;
import tdop.service.DashboardService;
import tdop.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getDashboardData(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQuickStats() {
        Long userId = getCurrentUserId();
        DashboardResponse data = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(Map.of(
            "savedCount", data.getSavedCount(),
            "applicationCount", data.getApplicationCount(),
            "unreadCount", data.getUnreadNotificationCount(),
            "profileCompletion", data.getProfileCompletion(),
            "availableOpportunities", data.getAvailableOpportunities()
        ));
    }

    @GetMapping("/deadlines")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingDeadlines() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getUpcomingDeadlines(userId));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Map<String, Object>>> getRecentApplications() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getRecentApplications(userId));
    }

    @GetMapping("/application-stats")
    public ResponseEntity<Map<String, Long>> getApplicationStats() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getApplicationStats(userId));
    }

    @GetMapping("/application-readiness")
    public ResponseEntity<Map<String, Object>> getApplicationReadiness() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getApplicationReadiness(userId));
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount() {
        Long userId = getCurrentUserId();
        long count = dashboardService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserIdByEmail(email);
    }
}
