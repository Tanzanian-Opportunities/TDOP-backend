package tdop.controller.trust;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.Appeal;
import tdop.repository.UserRepository;
import tdop.service.AppealService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trust/appeals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VERIFICATION_OFFICER', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN')")
public class AppealController {

    private final AppealService appealService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Appeal>> getAll() {
        return ResponseEntity.ok(appealService.getAllAppeals());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Appeal>> getPending() {
        return ResponseEntity.ok(appealService.getPendingAppeals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appeal> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appealService.getAppealById(id));
    }

    @PostMapping
    public ResponseEntity<Appeal> create(@RequestBody Map<String, Object> body, Authentication auth) {
        Long userId = getUserId(auth);
        String targetType = (String) body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId").toString());
        String reason = (String) body.get("reason");
        String description = (String) body.get("description");
        return ResponseEntity.ok(appealService.createAppeal(targetType, targetId, reason, description, userId));
    }

    @PostMapping("/{id}/uphold")
    public ResponseEntity<Appeal> uphold(@PathVariable Long id, @RequestParam String resolution) {
        return ResponseEntity.ok(appealService.upholdAppeal(id, resolution));
    }

    @PostMapping("/{id}/overrule")
    public ResponseEntity<Appeal> overrule(@PathVariable Long id, @RequestParam String resolution) {
        return ResponseEntity.ok(appealService.overruleAppeal(id, resolution));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<Appeal> dismiss(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(appealService.dismissAppeal(id, reason));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "pending", appealService.countPending()
        ));
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
    }
}
