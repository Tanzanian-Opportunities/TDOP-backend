package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Opportunity;
import tdop.entity.RiskSignal;
import tdop.entity.enums.RiskSignal.SignalType;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import tdop.repository.ReportRepository;
import tdop.repository.RiskSignalRepository;
import tdop.repository.UserRepository;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AntiFraudService {

    private final RiskSignalRepository riskSignalRepository;
    private final OpportunityRepository opportunityRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public void analyzeOpportunity(Long opportunityId) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        // Check for duplicate opportunities
        List<Opportunity> similar = opportunityRepository.findByTitleContainingIgnoreCase(opp.getTitle());
        if (similar.size() > 1) {
            createSignal("OPPORTUNITY", opportunityId, SignalType.DUPLICATE_OPPORTUNITY.name(),
                RiskSignal.RiskLevel.MEDIUM.name(), "Multiple opportunities with similar title found");
        }

        // Check for suspicious URLs
        if (opp.getSourceUrl() != null && !opp.getSourceUrl().isEmpty()) {
            String url = opp.getSourceUrl().toLowerCase();
            if (url.contains("bit.ly") || url.contains("tinyurl") || url.contains("t.co")) {
                createSignal("OPPORTUNITY", opportunityId, SignalType.SUSPICIOUS_URL.name(),
                    RiskSignal.RiskLevel.MEDIUM.name(), "Shortened URL detected");
            }
        }

        // Check if org is unverified
        if (opp.getCreatedBy() != null && !opp.getCreatedBy().isVerified()) {
            createSignal("OPPORTUNITY", opportunityId, SignalType.UNVERIFIED_ORG.name(),
                RiskSignal.RiskLevel.LOW.name(), "Published by unverified organization");
        }

        // Check report count
        long reportCount = reportRepository.findByTargetTypeAndTargetId("OPPORTUNITY", opportunityId).size();
        if (reportCount >= 3) {
            createSignal("OPPORTUNITY", opportunityId, SignalType.REPEATED_REPORTS.name(),
                RiskSignal.RiskLevel.HIGH.name(), "Multiple reports received: " + reportCount);
        }
    }

    public void analyzeOrganization(Long orgId) {
        long reportCount = reportRepository.findByTargetTypeAndTargetId("ORGANIZATION", orgId).size();
        if (reportCount >= 2) {
            createSignal("ORGANIZATION", orgId, SignalType.REPEATED_REPORTS.name(),
                RiskSignal.RiskLevel.HIGH.name(), "Multiple reports against organization");
        }
    }

    private void createSignal(String targetType, Long targetId, String signalType, String riskLevel, String description) {
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
        signal.setReviewedBy(userRepository.findById(reviewerId).orElse(null));
        return riskSignalRepository.save(signal);
    }

    public long countByLevel(String level) {
        return riskSignalRepository.countByRiskLevel(level);
    }

    public long countUnreviewed() {
        return riskSignalRepository.findByReviewedFalse().size();
    }
}
