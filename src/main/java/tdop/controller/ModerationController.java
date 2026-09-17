package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.ModerationAction;
import tdop.service.ModerationService;
import tdop.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;
    private final UserService userService;

    @GetMapping("/queue")
    public ResponseEntity<?> queue() {
        return ResponseEntity.ok(moderationService.getPendingModerationQueue());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ModerationAction> approve(@PathVariable Long id,
                                                     @RequestParam(required = false) String reason,
                                                     Authentication auth) {
        Long moderatorId = getUserId(auth);
        return ResponseEntity.ok(moderationService.approve(id, moderatorId, reason));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ModerationAction> reject(@PathVariable Long id,
                                                    @RequestParam(required = false) String reason,
                                                    Authentication auth) {
        Long moderatorId = getUserId(auth);
        return ResponseEntity.ok(moderationService.reject(id, moderatorId, reason));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<ModerationAction> suspend(@PathVariable Long id,
                                                     @RequestParam(required = false) String reason,
                                                     Authentication auth) {
        Long moderatorId = getUserId(auth);
        return ResponseEntity.ok(moderationService.suspend(id, moderatorId, reason));
    }

    @PostMapping("/{id}/request-info")
    public ResponseEntity<ModerationAction> requestInfo(@PathVariable Long id,
                                                         @RequestParam String details,
                                                         Authentication auth) {
        Long moderatorId = getUserId(auth);
        return ResponseEntity.ok(moderationService.requestInformation(id, moderatorId, details));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ModerationAction>> history(@PathVariable Long id) {
        return ResponseEntity.ok(moderationService.getHistory(id));
    }

    private Long getUserId(Authentication auth) {
        return userService.getUserIdByEmail(auth.getName());
    }
}
