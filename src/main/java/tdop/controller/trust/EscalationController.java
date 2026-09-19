package tdop.controller.trust;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.Escalation;
import tdop.repository.UserRepository;
import tdop.service.EscalationService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trust/escalations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VERIFICATION_OFFICER', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN')")
public class EscalationController {

    private final EscalationService escalationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Escalation>> getAll() {
        return ResponseEntity.ok(escalationService.getAllEscalations());
    }

    @GetMapping("/open")
    public ResponseEntity<List<Escalation>> getOpen() {
        return ResponseEntity.ok(escalationService.getOpenEscalations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Escalation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(escalationService.getEscalationById(id));
    }

    @PostMapping
    public ResponseEntity<Escalation> create(@RequestBody Map<String, Object> body, Authentication auth) {
        Long userId = getUserId(auth);
        String targetType = (String) body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId").toString());
        String reason = (String) body.get("reason");
        String description = (String) body.get("description");
        return ResponseEntity.ok(escalationService.createEscalation(targetType, targetId, reason, description, userId));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Escalation> assign(@PathVariable Long id, @RequestParam Long officerId) {
        return ResponseEntity.ok(escalationService.assignEscalation(id, officerId));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Escalation> resolve(@PathVariable Long id, @RequestParam String resolution) {
        return ResponseEntity.ok(escalationService.resolveEscalation(id, resolution));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<Escalation> dismiss(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(escalationService.dismissEscalation(id, reason));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "open", escalationService.countOpen()
        ));
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
    }
}
