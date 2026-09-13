package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tdop.service.SavedOpportunityService;
import tdop.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
public class SavedOpportunityController {

    private final SavedOpportunityService savedOpportunityService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> save(@RequestParam Long oppId) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(savedOpportunityService.saveOpportunity(userId, oppId));
    }

    @GetMapping
    public ResponseEntity<List<?>> list() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(savedOpportunityService.getSavedOpportunities(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> unsave(@RequestParam Long oppId) {
        Long userId = getCurrentUserId();
        savedOpportunityService.unsaveOpportunity(userId, oppId);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserIdByEmail(email);
    }
}
