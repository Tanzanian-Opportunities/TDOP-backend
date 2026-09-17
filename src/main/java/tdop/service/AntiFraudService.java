package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Opportunity;
import tdop.entity.RiskSignal;
import tdop.entity.RiskSignal.SignalType;
import tdop.entity.enums.NotificationType;
import tdop.exception.ResourceNotFoundException;
import tdop.notification.NotificationService;
import tdop.repository.OpportunityRepository;
import tdop.repository.ReportRepository;
import tdop.repository.RiskSignalRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AntiFraudService {

    private final RiskSignalRepository riskSignalRepository;
    private final OpportunityRepository opportunityRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void analyzeOpportunity(Long opportunityId) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        int riskCount = 0;

        // Check for duplicate opportunities with same title AND same org
        if (opp.getCreatedBy() != null) {
            List<Opportunity> sameOrgTitle = opportunityRepository.findByCreatedById(opp.getCreatedBy().getId()).stream()
                .filter(o -> !o.getId().equals(opportunityId))
                .filter(o -> o.getTitle() != null && o.getTitle().equalsIgnoreCase(opp.getTitle()))
                .toList();
            if (!sameOrgTitle.isEmpty()) {
                createSignal("OPPORTUNITY", opportunityId, SignalType.DUPLICATE_OPPORTUNITY.name(),
                    RiskSignal.RiskLevel.HIGH.name(), "Same organization posted identical title: " + opp.getTitle());
                riskCount++;
            }
        }

        // Check for similar title across platform (lower threshold)
        List<Opportunity> similar = opportunityRepository.findByTitleContainingIgnoreCase(opp.getTitle());
        if (similar.size() > 3) {
            createSignal("OPPORTUNITY", opportunityId, SignalType.DUPLICATE_OPPORTUNITY.name(),
                RiskSignal.RiskLevel.MEDIUM.name(), "Many similar titles found across platform: " + similar.size());
            riskCount++;
        }

        // Check for suspicious URLs
        if (opp.getSourceUrl() != null && !opp.getSourceUrl().isEmpty()) {
            String url = opp.getSourceUrl().toLowerCase();
            if (url.contains("bit.ly") || url.contains("tinyurl") || url.contains("t.co") || url.contains("is.gd")) {
                createSignal("OPPORTUNITY", opportunityId, SignalType.SUSPICIOUS_URL.name(),
                    RiskSignal.RiskLevel.MEDIUM.name(), "Shortened URL detected: " + opp.getSourceUrl());
                riskCount++;
            }
        }

        // Check if org is unverified
        if (opp.getCreatedBy() != null && !opp.getCreatedBy().isVerified()) {
            createSignal("OPPORTUNITY", opportunityId, SignalType.UNVERIFIED_ORG.name(),
                RiskSignal.RiskLevel.LOW.name(), "Published by unverified organization");
            riskCount++;
        }

        // Check report count
        long reportCount = reportRepository.findByTargetTypeAndTargetId("OPPORTUNITY", opportunityId).size();
        if (reportCount >= 3) {
            createSignal("OPPORTUNITY", opportunityId, SignalType.REPEATED_REPORTS.name(),
                RiskSignal.RiskLevel.HIGH.name(), "Multiple reports received: " + reportCount);
            riskCount++;
        }

        // Update risk score on the opportunity
        if (riskCount > 0) {
            String riskLevel = riskCount >= 3 ? "HIGH" : riskCount >= 2 ? "MEDIUM" : "LOW";
            opp.setRiskScore(riskLevel);
            opportunityRepository.save(opp);

            // Notify admin about high-risk opportunities
            if ("HIGH".equals(riskLevel)) {
                notifyAdmins("High Risk Opportunity Detected",
                    "Opportunity '" + opp.getTitle() + "' (ID: " + opp.getId() + ") scored " + riskCount + " risk signals.",
                    NotificationType.RISK);
            }
            log.info("Fraud analysis for opportunity id={}: {} risk signals detected (level={})", opportunityId, riskCount, riskLevel);
        }
    }

    @Transactional
    public void analyzeOrganization(Long orgId) {
        long reportCount = reportRepository.findByTargetTypeAndTargetId("ORGANIZATION", orgId).size();
        if (reportCount >= 2) {
            createSignal("ORGANIZATION", orgId, SignalType.REPEATED_REPORTS.name(),
                RiskSignal.RiskLevel.HIGH.name(), "Multiple reports against organization: " + reportCount);
        }
    }

    private void createSignal(String targetType, Long targetId, String signalType, String riskLevel, String description) {
        // Avoid duplicate signals for same target and type
        List<RiskSignal> existing = riskSignalRepository.findByTargetTypeAndTargetId(targetType, targetId);
        boolean alreadyExists = existing.stream()
            .anyMatch(s -> s.getSignalType().equals(signalType) && !s.isReviewed());
        if (alreadyExists) return;

        RiskSignal signal = RiskSignal.builder()
            .targetType(targetType)
            .targetId(targetId)
            .signalType(signalType)
            .riskLevel(riskLevel)
            .description(description)
            .autoDetected(true)
            .build();
        riskSignalRepository.save(signal);
    }

    public List<RiskSignal> getUnreviewedSignals() {
        return riskSignalRepository.findByReviewedFalse();
    }

    public List<RiskSignal> getSignalsForTarget(String targetType, Long targetId) {
        return riskSignalRepository.findByTargetTypeAndTargetId(targetType, targetId);
    }

    public RiskSignal reviewSignal(Long signalId, Long reviewerId) {
        RiskSignal signal = riskSignalRepository.findById(signalId)
            .orElseThrow(() -> new ResourceNotFoundException("Risk signal not found"));
        signal.setReviewed(true);
        signal.setReviewedAt(LocalDateTime.now());
        signal.setReviewedBy(userRepository.findById(reviewerId).orElse(null));
        return riskSignalRepository.save(signal);
    }

    public long countByLevel(String level) {
        return riskSignalRepository.countByRiskLevel(level);
    }

    public long countUnreviewed() {
        return riskSignalRepository.countUnreviewed();
    }

    private void notifyAdmins(String title, String message, NotificationType type) {
        try {
            var allUsers = userRepository.findAll();
            for (var user : allUsers) {
                if (user.getRole().name().equals("ADMIN") || user.getRole().name().equals("SUPER_ADMIN")) {
                    notificationService.createNotification(user.getId(), title, message, type);
                }
            }
        } catch (Exception e) {
            log.error("Failed to send admin notification: {}", e.getMessage());
        }
    }
}
