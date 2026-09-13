package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.VerificationRequest;
import tdop.entity.enums.VerificationStatus;
import tdop.service.VerificationOfficerService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/verification-officer")
@RequiredArgsConstructor
public class VerificationOfficerController {

    private final VerificationOfficerService verificationService;

    @GetMapping("/queue")
    public ResponseEntity<List<VerificationRequest>> pendingQueue() {
        return ResponseEntity.ok(verificationService.getPendingRequests());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<VerificationRequest> approve(@PathVariable Long id, Authentication auth) {
        String reviewedBy = auth.getName();
        return ResponseEntity.ok(verificationService.reviewRequest(id, VerificationStatus.APPROVED, reviewedBy, null));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<VerificationRequest> reject(@PathVariable Long id,
                                                       @RequestParam(required = false) String reason,
                                                       Authentication auth) {
        String reviewedBy = auth.getName();
        return ResponseEntity.ok(verificationService.reviewRequest(id, VerificationStatus.REJECTED, reviewedBy, reason));
    }

    @PostMapping("/{id}/request-info")
    public ResponseEntity<VerificationRequest> requestInfo(@PathVariable Long id,
                                                            @RequestParam String information) {
        return ResponseEntity.ok(verificationService.requestMoreInformation(id, information));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<?> documents(@PathVariable Long id) {
        return ResponseEntity.ok(verificationService.getDocuments(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "pending", verificationService.countPending()
        ));
    }
}
