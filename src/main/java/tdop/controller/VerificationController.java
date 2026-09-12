package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.VerificationRequest;
import tdop.verification.VerificationService;

@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<?> submit(@Valid @RequestBody VerificationRequest request,
                                     @RequestParam Long orgId) {
        return ResponseEntity.ok(verificationService.submitVerification(orgId, request.getDocument()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> status(@PathVariable Long id) {
        return ResponseEntity.ok(verificationService.getPendingRequests());
    }
}
