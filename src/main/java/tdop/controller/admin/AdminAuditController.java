package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.entity.AuditLog;
import tdop.repository.AuditLogRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AdminAuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<java.util.List<AuditLog>> list() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<java.util.List<AuditLog>> byEntity(@PathVariable String entityType) {
        return ResponseEntity.ok(auditLogRepository.findByEntityType(entityType));
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Long>> counts() {
        long opportunities = auditLogRepository.countByEntityType("OPPORTUNITY");
        long applications = auditLogRepository.countByEntityType("APPLICATION");
        long users = auditLogRepository.countByEntityType("USER");
        return ResponseEntity.ok(Map.of(
            "opportunities", opportunities,
            "applications", applications,
            "users", users,
            "total", opportunities + applications + users
        ));
    }
}
