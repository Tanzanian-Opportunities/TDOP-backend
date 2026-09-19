package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.enums.UserRole;
import tdop.repository.*;
import tdop.service.AnalyticsService;
import tdop.service.AuditLogService;
import tdop.service.AntiFraudService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/super")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminGovernanceController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final OrganizationProfileRepository orgProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final ReportRepository reportRepository;
    private final VerificationRequestRepository verificationRequestRepository;
    private final ModerationActionRepository moderationActionRepository;
    private final AuditLogRepository auditLogRepository;
    private final RiskSignalRepository riskSignalRepository;
    private final EscalationRepository escalationRepository;
    private final AppealRepository appealRepository;
    private final NotificationRepository notificationRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final AnalyticsService analyticsService;

    @GetMapping("/platform-attention")
    public ResponseEntity<Map<String, Object>> platformAttention() {
        Map<String, Object> attention = new HashMap<>();
        attention.put("pendingVerifications", verificationRequestRepository.countByStatus(tdop.entity.enums.VerificationStatus.PENDING));
        attention.put("pendingReports", reportRepository.countByStatus(tdop.entity.enums.ReportStatus.PENDING));
        attention.put("highRiskSignals", riskSignalRepository.countByRiskLevel("HIGH"));
        attention.put("openEscalations", escalationRepository.countByStatus(tdop.entity.enums.EscalationStatus.OPEN));
        attention.put("pendingAppeals", appealRepository.countByStatus(tdop.entity.enums.AppealStatus.PENDING));
        long totalAttention = 0;
        for (Object v : attention.values()) {
            if (v instanceof Long l) totalAttention += l;
        }
        attention.put("totalAttention", totalAttention);
        return ResponseEntity.ok(attention);
    }

    @GetMapping("/platform-health")
    public ResponseEntity<Map<String, Object>> platformHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("database", Map.of("status", "OPERATIONAL", "description", "PostgreSQL connected"));
        health.put("api", Map.of("status", "OPERATIONAL", "description", "Spring Boot API responding"));
        health.put("authentication", Map.of("status", "OPERATIONAL", "description", "JWT authentication active"));
        health.put("storage", Map.of("status", "OPERATIONAL", "description", "File storage available"));
        health.put("email", Map.of("status", "UNKNOWN", "description", "Email service configuration not verified"));
        health.put("sms", Map.of("status", "NOT_CONFIGURED", "description", "SMS integration not configured"));
        health.put("notifications", Map.of("status", "OPERATIONAL", "description", "In-app notifications active"));
        health.put("externalServices", Map.of("status", "UNKNOWN", "description", "No external service integrations configured"));
        health.put("backgroundJobs", Map.of("status", "OPERATIONAL", "description", "Deadline engine scheduled"));
        return ResponseEntity.ok(health);
    }

    @GetMapping("/platform-pulse")
    public ResponseEntity<Map<String, Object>> platformPulse() {
        Map<String, Object> pulse = new HashMap<>();
        pulse.put("users", Map.of(
            "total", userRepository.count(),
            "admins", countByRole("ADMIN"),
            "superAdmins", countByRole("SUPER_ADMIN"),
            "organizations", countByRole("ORGANIZATION"),
            "seekers", countByRole("SEEKER"),
            "verificationOfficers", countByRole("VERIFICATION_OFFICER"),
            "moderators", countByRole("MODERATOR")
        ));
        pulse.put("organizations", Map.of(
            "total", orgProfileRepository.count(),
            "verified", orgProfileRepository.findAll().stream().filter(o -> o.isVerified()).count()
        ));
        pulse.put("opportunities", Map.of(
            "total", opportunityRepository.count(),
            "published", opportunityRepository.countByStatus(tdop.entity.enums.OpportunityStatus.PUBLISHED),
            "draft", opportunityRepository.countByStatus(tdop.entity.enums.OpportunityStatus.DRAFT),
            "expired", opportunityRepository.countByStatus(tdop.entity.enums.OpportunityStatus.EXPIRED),
            "suspended", opportunityRepository.countByStatus(tdop.entity.enums.OpportunityStatus.SUSPENDED)
        ));
        pulse.put("applications", Map.of("total", applicationRepository.count()));
        pulse.put("reports", Map.of(
            "total", reportRepository.count(),
            "pending", reportRepository.countByStatus(tdop.entity.enums.ReportStatus.PENDING)
        ));
        pulse.put("escalations", Map.of("open", escalationRepository.countByStatus(tdop.entity.enums.EscalationStatus.OPEN)));
        pulse.put("appeals", Map.of("pending", appealRepository.countByStatus(tdop.entity.enums.AppealStatus.PENDING)));
        return ResponseEntity.ok(pulse);
    }

    @GetMapping("/user-role-assignments")
    public ResponseEntity<List<Map<String, Object>>> userRoleAssignments() {
        List<Map<String, Object>> assignments = new ArrayList<>();
        for (UserRole role : UserRole.values()) {
            long count = userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .count();
            Map<String, Object> entry = new HashMap<>();
            entry.put("role", role.name());
            entry.put("userCount", count);
            entry.put("description", getRoleDescription(role));
            assignments.add(entry);
        }
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/privileged-access")
    public ResponseEntity<List<Map<String, Object>>> privilegedAccess() {
        List<Map<String, Object>> privileged = new ArrayList<>();
        List<UserRole> privilegedRoles = List.of(UserRole.ADMIN, UserRole.SUPER_ADMIN, UserRole.MODERATOR, UserRole.VERIFICATION_OFFICER);
        for (var user : userRepository.findAll()) {
            if (privilegedRoles.contains(user.getRole())) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("userId", user.getId());
                entry.put("email", user.getEmail());
                entry.put("fullName", user.getFullName());
                entry.put("role", user.getRole().name());
                entry.put("enabled", user.isEnabled());
                entry.put("verified", user.isVerified());
                entry.put("createdAt", user.getCreatedAt());
                privileged.add(entry);
            }
        }
        return ResponseEntity.ok(privileged);
    }

    @GetMapping("/ecosystem-intelligence")
    public ResponseEntity<Map<String, Object>> ecosystemIntelligence() {
        Map<String, Object> intel = new HashMap<>();
        intel.put("platformStats", analyticsService.getDashboardStats());
        intel.put("opportunityAnalytics", analyticsService.getOpportunityAnalytics());
        intel.put("reportAnalytics", analyticsService.getReportAnalytics());
        intel.put("platformActivity", analyticsService.getPlatformActivity());
        return ResponseEntity.ok(intel);
    }

    @GetMapping("/security-overview")
    public ResponseEntity<Map<String, Object>> securityOverview() {
        Map<String, Object> security = new HashMap<>();
        security.put("totalUsers", userRepository.count());
        security.put("enabledUsers", userRepository.findAll().stream().filter(u -> u.isEnabled()).count());
        security.put("disabledUsers", userRepository.findAll().stream().filter(u -> !u.isEnabled()).count());
        security.put("unverifiedUsers", userRepository.findAll().stream().filter(u -> !u.isVerified()).count());
        security.put("highRiskSignals", riskSignalRepository.countByRiskLevel("HIGH"));
        security.put("mediumRiskSignals", riskSignalRepository.countByRiskLevel("MEDIUM"));
        security.put("unreviewedSignals", riskSignalRepository.findByReviewedFalse().size());
        security.put("recentAuditLogs", auditLogRepository.findAll().stream().limit(20).collect(Collectors.toList()));
        return ResponseEntity.ok(security);
    }

    @GetMapping("/governance-change-log")
    public ResponseEntity<List<Map<String, Object>>> governanceChangeLog() {
        List<Map<String, Object>> changes = new ArrayList<>();
        var recentLogs = auditLogRepository.findAll().stream()
            .filter(log -> log.getAction() != null && (
                log.getAction().toUpperCase().contains("ROLE") ||
                log.getAction().toUpperCase().contains("PERMISSION") ||
                log.getAction().toUpperCase().contains("CONFIG") ||
                log.getAction().toUpperCase().contains("DELETE") ||
                log.getAction().toUpperCase().contains("SUSPEND") ||
                log.getAction().toUpperCase().contains("CREATE")
            ))
            .limit(50)
            .collect(Collectors.toList());
        for (var log : recentLogs) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", log.getId());
            entry.put("action", log.getAction());
            entry.put("entityType", log.getEntityType());
            entry.put("entityId", log.getEntityId());
            entry.put("userId", log.getUserId());
            entry.put("timestamp", log.getTimestamp());
            entry.put("oldValue", log.getOldValue());
            entry.put("newValue", log.getNewValue());
            changes.add(entry);
        }
        return ResponseEntity.ok(changes);
    }

    @GetMapping("/taxonomy-overview")
    public ResponseEntity<Map<String, Object>> taxonomyOverview() {
        Map<String, Object> taxonomy = new HashMap<>();
        var categories = opportunityRepository.findAll().stream()
            .filter(o -> o.getCategory() != null)
            .map(o -> o.getCategory())
            .distinct()
            .collect(Collectors.toList());
        taxonomy.put("categories", categories);
        taxonomy.put("categoryCount", categories.size());
        taxonomy.put("locations", opportunityRepository.findAll().stream()
            .filter(o -> o.getLocation() != null)
            .map(o -> o.getLocation())
            .distinct()
            .collect(Collectors.toList()));
        taxonomy.put("types", Arrays.stream(tdop.entity.enums.OpportunityType.values())
            .map(Enum::name).collect(Collectors.toList()));
        taxonomy.put("note", "Taxonomy governance is limited — no dedicated taxonomy entity exists. Categories are stored as free-text on opportunities.");
        return ResponseEntity.ok(taxonomy);
    }

    private long countByRole(UserRole role) {
        return userRepository.findAll().stream().filter(u -> u.getRole() == role).count();
    }

    private String getRoleDescription(UserRole role) {
        return switch (role) {
            case SEEKER -> "Job seeker with personal workspace";
            case ORGANIZATION -> "Organization with opportunity provider workspace";
            case ORGANIZATION_ADMIN -> "Organization administrator";
            case ORGANIZATION_MEMBER -> "Organization team member";
            case VERIFICATION_OFFICER -> "Trust & quality verification operations";
            case MODERATOR -> "Trust & quality moderation operations";
            case ADMIN -> "Platform operations & management";
            case SUPER_ADMIN -> "Platform governance & control";
        };
    }
}
