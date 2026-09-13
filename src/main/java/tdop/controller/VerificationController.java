package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.entity.VerificationRequest;
import tdop.service.VerificationOfficerService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationOfficerService verificationService;

    @PostMapping
    public ResponseEntity<VerificationRequest> submit(@Valid @RequestBody tdop.dto.request.VerificationRequest request,
                                                       @RequestParam Long orgId) {
        return ResponseEntity.ok(verificationService.submitVerification(orgId, request.getDocument()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VerificationRequest> status(@PathVariable Long id) {
        return ResponseEntity.ok(verificationService.getRequestById(id));
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<VerificationRequest>> byOrganization(@PathVariable Long orgId) {
        return ResponseEntity.ok(verificationService.getRequestsByOrg(orgId));
    }
}
