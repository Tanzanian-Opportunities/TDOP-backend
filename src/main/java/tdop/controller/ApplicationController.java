package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.ApplicationRequest;
import tdop.entity.enums.ApplicationStatus;
import tdop.service.ApplicationService;
import tdop.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> apply(@Valid @RequestBody ApplicationRequest request,
                                   @RequestParam Long oppId) {
        Long applicantId = getCurrentUserId();
        return ResponseEntity.ok(applicationService.apply(oppId, applicantId,
            request.getCoverLetter(), request.getResumeUrl()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<?>> track() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplication(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(applicationService.getApplication(id, userId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ORGANIZATION_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam ApplicationStatus status) {
        Long operatorUserId = getCurrentUserId();
        return ResponseEntity.ok(applicationService.updateStatus(id, status, operatorUserId));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(applicationService.withdrawApplication(id, userId));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserIdByEmail(email);
    }
}
