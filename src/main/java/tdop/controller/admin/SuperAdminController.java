package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.enums.UserRole;
import tdop.rbac.RbacService;
import tdop.service.AnalyticsService;
import tdop.service.UserService;
import tdop.dto.response.UserResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/super")
@RequiredArgsConstructor
public class SuperAdminController {

    private final RbacService rbacService;
    private final AnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/roles")
    public ResponseEntity<?> roles() {
        return ResponseEntity.ok(rbacService.getAllRoles());
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> permissions() {
        return ResponseEntity.ok(rbacService.getAllPermissions());
    }

    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<?> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        rbacService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok("Role assigned");
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<?> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        rbacService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok("Role removed");
    }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<?> userPermissions(@PathVariable Long userId) {
        return ResponseEntity.ok(rbacService.getUserPermissions(userId));
    }

    @GetMapping("/platform-stats")
    public ResponseEntity<Map<String, Object>> platformStats() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/platform-activity")
    public ResponseEntity<Map<String, Object>> platformActivity() {
        return ResponseEntity.ok(analyticsService.getPlatformActivity());
    }
}
