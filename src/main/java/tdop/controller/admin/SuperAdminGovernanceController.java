package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tdop.entity.enums.UserRole;
import tdop.repository.*;
import tdop.service.AnalyticsService;
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
        String dbStatus = "OPERATIONAL";
        String dbDesc = "PostgreSQL connected";
        try { userRepository.count(); } catch (Exception e) { dbStatus = "ERROR"; dbDesc = "Database error: " + e.getMessage(); }
        health.put("database", Map.of("status", dbStatus, "description", dbDesc));
        health.put("api", Map.of("status", "OPERATIONAL", "description", "Spring Boot API responding"));
        health.put("authentication", Map.of("status", "OPERATIONAL", "description", "JWT authentication active"));
        String storageStatus = "OPERATIONAL";
        String storageDesc = "File storage available";
        try { new java.io.File("uploads").exists(); } catch (Exception e) { storageStatus = "UNKNOWN"; storageDesc = "Storage check failed"; }
        health.put("storage", Map.of("status", storageStatus, "description", storageDesc));
        health.put("email", Map.of("status", "UNKNOWN", "description", "Email service configuration not verified"));
        health.put("sms", Map.of("status", "NOT_CONFIGURED", "description", "SMS integration not configured"));
        health.put("notifications", Map.of("status", "OPERATIONAL", "description", "In-app notifications active"));
        health.put("externalServices", Map.of("status", "UNKNOWN", "description", "No external service integrations configured"));
        health.put("backgroundJobs", Map.of("status", "OPERATIONAL", "description", "Deadline engine scheduled (@Scheduled fixedRate=3600000)"));
        return ResponseEntity.ok(health);
    }

    @GetMapping("/platform-pulse")
    public ResponseEntity<Map<String, Object>> platformPulse() {
        Map<String, Object> pulse = new HashMap<>();
        pulse.put("users", Map.of(
            "total", userRepository.count(),
            "admins", countByRole(UserRole.ADMIN),
            "superAdmins", countByRole(UserRole.SUPER_ADMIN),
            "organizations", countByRole(UserRole.ORGANIZATION),
            "seekers", countByRole(UserRole.SEEKER),
            "verificationOfficers", countByRole(UserRole.VERIFICATION_OFFICER),
            "moderators", countByRole(UserRole.MODERATOR)
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

    @GetMapping("/feature-flags")
    public ResponseEntity<List<Map<String, Object>>> featureFlags() {
        List<Map<String, Object>> flags = new ArrayList<>();
        platformConfigRepository.findAll().stream()
            .filter(c -> c.getConfigKey() != null && c.getConfigKey().startsWith("feature."))
            .forEach(c -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("key", c.getConfigKey());
                entry.put("value", c.getConfigValue());
                entry.put("description", c.getDescription());
                entry.put("updatedAt", c.getUpdatedAt());
                flags.add(entry);
            });
        return ResponseEntity.ok(flags);
    }

    @PostMapping("/feature-flags")
    public ResponseEntity<Map<String, Object>> setFeatureFlag(@RequestBody Map<String, String> body) {
        String key = body.getOrDefault("key", "");
        String value = body.getOrDefault("value", "true");
        String description = body.getOrDefault("description", "");
        if (!key.startsWith("feature.")) key = "feature." + key;
        var config = platformConfigRepository.findByConfigKey(key)
            .orElse(tdop.entity.PlatformConfig.builder().configKey(key).build());
        config.setConfigValue(value);
        config.setDescription(description);
        platformConfigRepository.save(config);
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("description", description);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/feature-flags/{key}")
    public ResponseEntity<Map<String, String>> deleteFeatureFlag(@PathVariable String key) {
        platformConfigRepository.findByConfigKey(key).ifPresent(platformConfigRepository::delete);
        return ResponseEntity.ok(Map.of("status", "deleted", "key", key));
    }

    @GetMapping("/session-overview")
    public ResponseEntity<Map<String, Object>> sessionOverview() {
        Map<String, Object> session = new HashMap<>();
        session.put("totalUsers", userRepository.count());
        session.put("enabledUsers", userRepository.findAll().stream().filter(u -> u.isEnabled()).count());
        session.put("disabledUsers", userRepository.findAll().stream().filter(u -> !u.isEnabled()).count());
        session.put("adminSessions", userRepository.findAll().stream()
            .filter(u -> u.getRole() == UserRole.ADMIN || u.getRole() == UserRole.SUPER_ADMIN)
            .filter(u -> u.isEnabled())
            .count());
        session.put("privilegedActive", userRepository.findAll().stream()
            .filter(u -> List.of(UserRole.ADMIN, UserRole.SUPER_ADMIN, UserRole.MODERATOR, UserRole.VERIFICATION_OFFICER).contains(u.getRole()))
            .filter(u -> u.isEnabled())
            .count());
        session.put("note", "Session management is stateless (JWT). Revocation is handled via token blacklist. Active sessions cannot be listed without a session store.");
        return ResponseEntity.ok(session);
    }

    @GetMapping("/notification-config")
    public ResponseEntity<List<Map<String, Object>>> notificationConfig() {
        List<Map<String, Object>> configs = new ArrayList<>();
        platformConfigRepository.findAll().stream()
            .filter(c -> c.getConfigKey() != null && c.getConfigKey().startsWith("notification."))
            .forEach(c -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("key", c.getConfigKey());
                entry.put("value", c.getConfigValue());
                entry.put("description", c.getDescription());
                entry.put("updatedAt", c.getUpdatedAt());
                configs.add(entry);
            });
        if (configs.isEmpty()) {
            configs.add(Map.of("key", "notification.email.enabled", "value", "true", "description", "Enable email notifications"));
            configs.add(Map.of("key", "notification.inapp.enabled", "value", "true", "description", "Enable in-app notifications"));
            configs.add(Map.of("key", "notification.sms.enabled", "value", "false", "description", "Enable SMS notifications"));
            configs.add(Map.of("key", "notification.deadline_reminders", "value", "true", "description", "Send deadline reminder notifications"));
        }
        return ResponseEntity.ok(configs);
    }

    @PostMapping("/notification-config")
    public ResponseEntity<Map<String, Object>> setNotificationConfig(@RequestBody Map<String, String> body) {
        String key = body.getOrDefault("key", "");
        String value = body.getOrDefault("value", "true");
        String description = body.getOrDefault("description", "");
        if (!key.startsWith("notification.")) key = "notification." + key;
        var config = platformConfigRepository.findByConfigKey(key)
            .orElse(tdop.entity.PlatformConfig.builder().configKey(key).build());
        config.setConfigValue(value);
        config.setDescription(description);
        platformConfigRepository.save(config);
        return ResponseEntity.ok(Map.of("key", key, "value", value, "description", description));
    }

    @GetMapping("/integration-config")
    public ResponseEntity<List<Map<String, Object>>> integrationConfig() {
        List<Map<String, Object>> configs = new ArrayList<>();
        platformConfigRepository.findAll().stream()
            .filter(c -> c.getConfigKey() != null && c.getConfigKey().startsWith("integration."))
            .forEach(c -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("key", c.getConfigKey());
                entry.put("value", c.getConfigValue());
                entry.put("description", c.getDescription());
                entry.put("updatedAt", c.getUpdatedAt());
                configs.add(entry);
            });
        if (configs.isEmpty()) {
            configs.add(Map.of("key", "integration.email.provider", "value", "NOT_CONFIGURED", "description", "Email service provider"));
            configs.add(Map.of("key", "integration.sms.provider", "value", "NOT_CONFIGURED", "description", "SMS service provider"));
            configs.add(Map.of("key", "integration.storage.provider", "value", "LOCAL", "description", "File storage provider"));
            configs.add(Map.of("key", "integration.analytics.enabled", "value", "false", "description", "Enable analytics tracking"));
        }
        return ResponseEntity.ok(configs);
    }

    @PostMapping("/integration-config")
    public ResponseEntity<Map<String, Object>> setIntegrationConfig(@RequestBody Map<String, String> body) {
        String key = body.getOrDefault("key", "");
        String value = body.getOrDefault("value", "");
        String description = body.getOrDefault("description", "");
        if (!key.startsWith("integration.")) key = "integration." + key;
        var config = platformConfigRepository.findByConfigKey(key)
            .orElse(tdop.entity.PlatformConfig.builder().configKey(key).build());
        config.setConfigValue(value);
        config.setDescription(description);
        platformConfigRepository.save(config);
        return ResponseEntity.ok(Map.of("key", key, "value", value, "description", description));
    }

    @GetMapping("/background-jobs")
    public ResponseEntity<Map<String, Object>> backgroundJobs() {
        Map<String, Object> jobs = new HashMap<>();
        List<Map<String, Object>> jobList = new ArrayList<>();
        jobList.add(Map.of(
            "name", "DeadlineEngineService.processDeadlines",
            "schedule", "Every hour (3600000ms)",
            "status", "SCHEDULED",
            "description", "Marks expired opportunities, marks closing-soon, sends deadline reminders"
        ));
        jobList.add(Map.of(
            "name", "DeadlineEngineService.markExpiredOpportunities",
            "schedule", "Called by processDeadlines",
            "status", "ACTIVE",
            "description", "Marks opportunities past deadline as EXPIRED"
        ));
        jobList.add(Map.of(
            "name", "DeadlineEngineService.markClosingSoonOpportunities",
            "schedule", "Called by processDeadlines",
            "status", "ACTIVE",
            "description", "Marks published opportunities approaching deadline as CLOSING_SOON"
        ));
        jobList.add(Map.of(
            "name", "DeadlineEngineService.sendDeadlineReminders",
            "schedule", "Called by processDeadlines",
            "status", "ACTIVE",
            "description", "Creates deadline reminder records for upcoming deadlines"
        ));
        jobs.put("jobs", jobList);
        jobs.put("totalJobs", jobList.size());
        jobs.put("activeJobs", jobList.size());
        jobs.put("failedJobs", 0);
        jobs.put("note", "Background jobs are managed by Spring @Scheduled. No distributed job queue is configured.");
        return ResponseEntity.ok(jobs);
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
