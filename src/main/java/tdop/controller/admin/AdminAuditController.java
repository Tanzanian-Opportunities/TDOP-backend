package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tdop.entity.AuditLog;
import tdop.repository.AuditLogRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> result = auditLogRepository.findByFilters(action, entityType, pageRequest);
        return ResponseEntity.ok(Map.of(
            "data", result.getContent(),
            "total", result.getTotalElements(),
            "page", result.getNumber(),
            "limit", result.getSize(),
            "totalPages", result.getTotalPages()
        ));
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
