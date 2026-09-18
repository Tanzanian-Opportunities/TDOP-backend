package tdop.controller.trust;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tdop.service.*;

import java.util.HashMap;
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
}
