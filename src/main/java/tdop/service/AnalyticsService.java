package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.enums.ReportStatus;
import tdop.entity.enums.VerificationStatus;
import tdop.repository.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AuditLogRepository auditLogRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final VerificationRequestRepository verificationRequestRepository;
    private final ReportRepository reportRepository;
    private final RiskSignalRepository riskSignalRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("verifiedOrganizations", organizationProfileRepository.findAll().stream()
            .filter(o -> o.isVerified()).count());
        stats.put("pendingVerifications", verificationRequestRepository.countByStatus(VerificationStatus.PENDING));
        stats.put("activeOpportunities", opportunityRepository.countByStatus(OpportunityStatus.PUBLISHED)
            + opportunityRepository.countByStatus(OpportunityStatus.CLOSING_SOON));
        stats.put("pendingModeration", opportunityRepository.findPendingModeration().size());
        stats.put("totalOrganizations", organizationProfileRepository.count());
        stats.put("pendingReports", reportRepository.countByStatus(ReportStatus.PENDING));
        stats.put("totalReports", reportRepository.count());
        stats.put("highRiskSignals", riskSignalRepository.countByRiskLevel("HIGH"));
        stats.put("suspendedOpportunities", opportunityRepository.countByStatus(OpportunityStatus.SUSPENDED));
        return stats;
    }

    public Map<String, Object> getOpportunityAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        Map<String, Long> byStatus = new HashMap<>();
        for (OpportunityStatus status : OpportunityStatus.values()) {
            byStatus.put(status.name(), opportunityRepository.countByStatus(status));
        }
        analytics.put("totalByStatus", byStatus);
        analytics.put("totalPublished", opportunityRepository.countByStatus(OpportunityStatus.PUBLISHED));
        analytics.put("totalDraft", opportunityRepository.countByStatus(OpportunityStatus.DRAFT));
        analytics.put("totalExpired", opportunityRepository.countByStatus(OpportunityStatus.EXPIRED));
        return analytics;
    }

    public Map<String, Object> getReportAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalReports", reportRepository.count());
        analytics.put("pendingReports", reportRepository.countByStatus(ReportStatus.PENDING));
        analytics.put("reviewedReports", reportRepository.countByStatus(ReportStatus.REVIEWED));
        analytics.put("actionedReports", reportRepository.countByStatus(ReportStatus.ACTIONED));
        return analytics;
    }

    public Map<String, Object> getOrganizationAnalytics(Long orgId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalOpportunities", opportunityRepository.countByCreatedByUserId(orgId));
        analytics.put("publishedOpportunities", opportunityRepository.findByCreatedById(orgId).stream()
            .filter(o -> o.getStatus() == OpportunityStatus.PUBLISHED).count());
        analytics.put("totalApplications", opportunityRepository.findByCreatedById(orgId).stream()
            .mapToLong(o -> applicationRepository.findByOpportunityId(o.getId()).size()).sum());
        return analytics;
    }

    public Map<String, Object> getPlatformActivity() {
        Map<String, Object> activity = new HashMap<>();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        activity.put("newUsersLast7Days", userRepository.countCreatedSince(sevenDaysAgo));
        activity.put("newOpportunitiesLast7Days", opportunityRepository.countCreatedSince(sevenDaysAgo));
        activity.put("newApplicationsLast7Days", applicationRepository.countCreatedSince(sevenDaysAgo));
        return activity;
    }
}
