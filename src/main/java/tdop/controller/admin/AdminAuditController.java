package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.AuditLog;
import tdop.repository.AuditLogRepository;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<AuditLog>> list() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<AuditLog>> byEntity(@PathVariable String entityType) {
        return ResponseEntity.ok(auditLogRepository.findAll().stream()
            .filter(a -> entityType.equals(a.getEntityType()))
            .toList());
    }
}
