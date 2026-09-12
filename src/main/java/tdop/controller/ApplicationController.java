package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.ApplicationRequest;
import tdop.entity.ApplicationStatus;
import tdop.service.ApplicationService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<?> apply(@Valid @RequestBody ApplicationRequest request,
                                       @RequestParam Long oppId,
                                       @RequestParam Long applicantId) {
        return ResponseEntity.ok(applicationService.apply(oppId, applicantId,
            request.getCoverLetter(), request.getResumeUrl()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<?>> track(@RequestParam Long userId) {
        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                              @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }
}
