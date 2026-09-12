package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.service.SavedOpportunityService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
public class SavedOpportunityController {

    private final SavedOpportunityService savedOpportunityService;

    @PostMapping
    public ResponseEntity<?> save(@RequestParam Long userId, @RequestParam Long oppId) {
        return ResponseEntity.ok(savedOpportunityService.saveOpportunity(userId, oppId));
    }

    @GetMapping
    public ResponseEntity<List<?>> list(@RequestParam Long userId) {
        return ResponseEntity.ok(savedOpportunityService.getSavedOpportunities(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> unsave(@RequestParam Long userId, @RequestParam Long oppId) {
        savedOpportunityService.unsaveOpportunity(userId, oppId);
        return ResponseEntity.ok().build();
    }
}
