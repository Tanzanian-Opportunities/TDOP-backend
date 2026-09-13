package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.service.AntiFraudService;
import tdop.entity.RiskSignal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/fraud")
@RequiredArgsConstructor
public class AdminFraudController {

    private final AntiFraudService antiFraudService;

    @GetMapping("/signals")
    public ResponseEntity<List<RiskSignal>> unreviewedSignals() {
        return ResponseEntity.ok(antiFraudService.getUnreviewedSignals());
    }

    @GetMapping("/signals/{targetType}/{targetId}")
    public ResponseEntity<List<RiskSignal>> signalsForTarget(@PathVariable String targetType,
                                                              @PathVariable Long targetId) {
        return ResponseEntity.ok(antiFraudService.getSignalsForTarget(targetType, targetId));
    }

    @PostMapping("/signals/{id}/review")
    public ResponseEntity<RiskSignal> reviewSignal(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(antiFraudService.reviewSignal(id, userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "highRisk", antiFraudService.countByLevel("HIGH"),
            "mediumRisk", antiFraudService.countByLevel("MEDIUM"),
            "lowRisk", antiFraudService.countByLevel("LOW"),
            "unreviewed", antiFraudService.countUnreviewed()
        ));
    }

    private Long getUserId(Authentication auth) {
        return null;
    }
}
