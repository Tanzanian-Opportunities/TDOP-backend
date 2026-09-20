package tdop.controller.trust;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.VerificationDocument;
import tdop.entity.ModerationAction;
import tdop.entity.Report;
import tdop.entity.User;
import tdop.dto.response.UserResponse;
import tdop.repository.UserRepository;
import tdop.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trust")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VERIFICATION_OFFICER', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN')")
public class TrustDashboardController {

    private final VerificationOfficerService verificationService;
    private final ModerationService moderationService;
    private final ReportInvestigationService reportService;
    private final AntiFraudService antiFraudService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @GetMapping("/attention")
    public ResponseEntity<Map<String, Object>> attentionCenter() {
        Map<String, Object> attention = new HashMap<>();
        attention.put("pendingVerifications", verificationService.countPending());
        attention.put("pendingModeration", moderationService.getPendingModerationQueue().size());
        attention.put("pendingReports", reportService.countPending());
        attention.put("highRiskSignals", antiFraudService.countByLevel("HIGH"));
        attention.put("mediumRiskSignals", antiFraudService.countByLevel("MEDIUM"));
        attention.put("unreviewedSignals", antiFraudService.countUnreviewed());
        return ResponseEntity.ok(attention);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> operationalStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingVerifications", verificationService.countPending());
        stats.put("pendingModeration", moderationService.getPendingModerationQueue().size());
        stats.put("reportStats", reportService.getReportAnalytics());
        stats.put("fraudStats", Map.of(
            "highRisk", antiFraudService.countByLevel("HIGH"),
            "mediumRisk", antiFraudService.countByLevel("MEDIUM"),
            "lowRisk", antiFraudService.countByLevel("LOW"),
            "unreviewed", antiFraudService.countUnreviewed()
        ));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/queue")
    public ResponseEntity<Map<String, Object>> combinedWorkQueue(
            @RequestParam(defaultValue = "all") String type) {
        Map<String, Object> queue = new HashMap<>();
        if ("all".equals(type) || "verification".equals(type)) {
            queue.put("verifications", verificationService.getPendingRequests());
        }
        if ("all".equals(type) || "moderation".equals(type)) {
            queue.put("moderation", moderationService.getPendingModerationQueue());
        }
        if ("all".equals(type) || "reports".equals(type)) {
            queue.put("reports", reportService.getPendingReports());
        }
        if ("all".equals(type) || "fraud".equals(type)) {
            queue.put("fraudSignals", antiFraudService.getUnreviewedSignals());
        }
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/verification/{id}/documents")
    public ResponseEntity<List<VerificationDocument>> verificationDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(verificationService.getDocuments(id));
    }

    @GetMapping("/verification/{id}")
    public ResponseEntity<?> verificationDetail(@PathVariable Long id) {
        return ResponseEntity.ok(verificationService.getRequestById(id));
    }

    @GetMapping("/moderation/{id}/history")
    public ResponseEntity<List<ModerationAction>> moderationHistory(@PathVariable Long id) {
        return ResponseEntity.ok(moderationService.getHistory(id));
    }

    @PostMapping("/reports/{id}/assign")
    public ResponseEntity<Report> assignReport(@PathVariable Long id,
                                                @RequestParam Long investigatorId) {
        return ResponseEntity.ok(reportService.assignToInvestigator(id, investigatorId));
    }

    @PostMapping("/reports/{id}/add-notes")
    public ResponseEntity<Report> addReportNotes(@PathVariable Long id,
                                                  @RequestParam String notes) {
        return ResponseEntity.ok(reportService.addInvestigationNotes(id, notes));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getTrustOfficers() {
        List<UserResponse> officers = userRepository.findAll().stream()
            .map(u -> UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .role(u.getRole().name())
                .enabled(u.isEnabled())
                .verified(u.isVerified())
                .build())
            .toList();
        return ResponseEntity.ok(officers);
    }
}
