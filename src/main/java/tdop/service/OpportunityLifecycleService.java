package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Opportunity;
import tdop.entity.OpportunityStatusHistory;
import tdop.entity.OrganizationProfile;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.enums.OpportunityType;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import tdop.repository.OpportunityStatusHistoryRepository;
import tdop.repository.OrganizationProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OpportunityLifecycleService {

    private final OpportunityRepository opportunityRepository;
    private final OrganizationProfileRepository organizationRepository;
    private final OpportunityStatusHistoryRepository statusHistoryRepository;
    private final AntiFraudService antiFraudService;

    public OpportunityResponse createDraft(OpportunityRequest request, Long orgId) {
        OrganizationProfile org = organizationRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        Opportunity opp = Opportunity.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .requirements(request.getRequirements())
            .benefits(request.getBenefits())
            .location(request.getLocation())
            .type(request.getType() != null ? OpportunityType.valueOf(request.getType().toUpperCase()) : null)
            .category(request.getCategory())
            .salaryRange(request.getSalaryRange())
            .tags(request.getTags())
            .deadline(request.getDeadline())
            .sourceUrl(request.getSourceUrl())
            .applicationUrl(request.getApplicationUrl())
            .workMode(request.getWorkMode() != null ? request.getWorkMode() : "ONSITE")
            .educationLevel(request.getEducationLevel())
            .experienceLevel(request.getExperienceLevel())
            .fundingInfo(request.getFundingInfo())
            .eligibility(request.getEligibility())
            .requiredDocuments(request.getRequiredDocuments())
            .status(OpportunityStatus.DRAFT)
            .createdBy(org)
            .build();
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse submitForReview(Long id, Long userId) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        transitionStatus(opp, OpportunityStatus.SUBMITTED, "Submitted for review");
        Opportunity saved = opportunityRepository.save(opp);
        try {
            antiFraudService.analyzeOpportunity(saved.getId());
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(OpportunityLifecycleService.class)
                .warn("Fraud analysis failed for opportunity id={}: {}", id, e.getMessage());
        }
        return toResponse(saved);
    }

    public OpportunityResponse verifyOpportunity(Long id, Long verifierId, boolean approved, String reason) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (approved) {
            transitionStatus(opp, OpportunityStatus.VERIFIED, reason);
            opp.setVerified(true);
            opp.setVerifiedAt(LocalDateTime.now());
        } else {
            transitionStatus(opp, OpportunityStatus.REJECTED, reason);
        }
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse moderateOpportunity(Long id, Long moderatorId, String action, String reason) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        switch (action.toUpperCase()) {
            case "APPROVE":
                transitionStatus(opp, OpportunityStatus.APPROVED, reason);
                break;
            case "REJECT":
                transitionStatus(opp, OpportunityStatus.REJECTED, reason);
                break;
            case "SUSPEND":
                transitionStatus(opp, OpportunityStatus.SUSPENDED, reason);
                break;
            default:
                throw new BadRequestException("Invalid moderation action: " + action);
        }
        opp.setModerated(true);
        opp.setModeratedAt(LocalDateTime.now());
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse publish(Long id, Long userId) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        if (opp.getStatus() != OpportunityStatus.APPROVED && opp.getStatus() != OpportunityStatus.DRAFT) {
            throw new BadRequestException("Opportunity must be approved before publishing");
        }
        transitionStatus(opp, OpportunityStatus.PUBLISHED, "Published");
        opp.setPublishedAt(LocalDateTime.now());
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse suspend(Long id, Long userId, String reason) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        transitionStatus(opp, OpportunityStatus.SUSPENDED, reason);
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse archive(Long id, Long userId) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        transitionStatus(opp, OpportunityStatus.ARCHIVED, "Archived by owner");
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse restore(Long id, Long userId) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        transitionStatus(opp, OpportunityStatus.PUBLISHED, "Restored");
        return toResponse(opportunityRepository.save(opp));
    }

    public void markExpired() {
        List<Opportunity> expired = opportunityRepository.findExpiredNotMarked();
        for (Opportunity opp : expired) {
            transitionStatus(opp, OpportunityStatus.EXPIRED, "Auto-expired");
            opportunityRepository.save(opp);
        }
    }

    public void markClosingSoon() {
        List<Opportunity> closingSoon = opportunityRepository.findClosingSoon();
        for (Opportunity opp : closingSoon) {
            if (opp.getStatus() == OpportunityStatus.PUBLISHED) {
                opp.setStatus(OpportunityStatus.CLOSING_SOON);
                opp.setClosingSoonNotified(true);
                opportunityRepository.save(opp);
            }
        }
    }

    public List<OpportunityStatusHistory> getStatusHistory(Long opportunityId) {
        return statusHistoryRepository.findByOpportunityIdOrderByCreatedAtDesc(opportunityId);
    }

    public List<OpportunityResponse> getOpportunitiesByOrg(Long orgId) {
        return opportunityRepository.findByCreatedById(orgId).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OpportunityResponse> getPendingReview() {
        return opportunityRepository.findPendingModeration().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public OpportunityResponse updateOpportunity(Long id, Long userId, OpportunityRequest request) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        opp.setTitle(request.getTitle());
        opp.setDescription(request.getDescription());
        opp.setRequirements(request.getRequirements());
        opp.setBenefits(request.getBenefits());
        opp.setLocation(request.getLocation());
        if (request.getType() != null) opp.setType(OpportunityType.valueOf(request.getType().toUpperCase()));
        opp.setCategory(request.getCategory());
        opp.setSalaryRange(request.getSalaryRange());
        opp.setTags(request.getTags());
        opp.setDeadline(request.getDeadline());
        if (request.getSourceUrl() != null) opp.setSourceUrl(request.getSourceUrl());
        if (request.getApplicationUrl() != null) opp.setApplicationUrl(request.getApplicationUrl());
        if (request.getWorkMode() != null) opp.setWorkMode(request.getWorkMode());
        if (request.getEducationLevel() != null) opp.setEducationLevel(request.getEducationLevel());
        if (request.getExperienceLevel() != null) opp.setExperienceLevel(request.getExperienceLevel());
        if (request.getFundingInfo() != null) opp.setFundingInfo(request.getFundingInfo());
        if (request.getEligibility() != null) opp.setEligibility(request.getEligibility());
        if (request.getRequiredDocuments() != null) opp.setRequiredDocuments(request.getRequiredDocuments());
        return toResponse(opportunityRepository.save(opp));
    }

    public void deleteOpportunity(Long id, Long userId) {
        Opportunity opp = getAndValidateOwnership(id, userId);
        opportunityRepository.delete(opp);
    }

    private Opportunity getAndValidateOwnership(Long id, Long userId) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (opp.getCreatedBy() == null || !opp.getCreatedBy().getUser().getId().equals(userId)) {
            throw new ForbiddenException("Not authorized to modify this opportunity");
        }
        return opp;
    }

    private void transitionStatus(Opportunity opp, OpportunityStatus newStatus, String reason) {
        OpportunityStatus oldStatus = opp.getStatus();
        opp.setStatus(newStatus);
        OpportunityStatusHistory history = OpportunityStatusHistory.builder()
            .opportunity(opp)
            .oldStatus(oldStatus != null ? oldStatus.name() : null)
            .newStatus(newStatus.name())
            .reason(reason)
            .build();
        statusHistoryRepository.save(history);
    }

    public OpportunityResponse toResponse(Opportunity opp) {
        return OpportunityResponse.builder()
            .id(opp.getId())
            .title(opp.getTitle())
            .description(opp.getDescription())
            .location(opp.getLocation())
            .type(opp.getType() != null ? opp.getType().name() : null)
            .category(opp.getCategory())
            .salaryRange(opp.getSalaryRange())
            .status(opp.getStatus().name())
            .deadline(opp.getDeadline())
            .tags(opp.getTags())
            .createdAt(opp.getCreatedAt())
            .updatedAt(opp.getUpdatedAt())
            .sourceUrl(opp.getSourceUrl())
            .applicationUrl(opp.getApplicationUrl())
            .workMode(opp.getWorkMode())
            .educationLevel(opp.getEducationLevel())
            .experienceLevel(opp.getExperienceLevel())
            .fundingInfo(opp.getFundingInfo())
            .requirements(opp.getRequirements())
            .benefits(opp.getBenefits())
            .eligibility(opp.getEligibility())
            .requiredDocuments(opp.getRequiredDocuments())
            .verified(opp.isVerified())
            .verifiedAt(opp.getVerifiedAt())
            .moderated(opp.isModerated())
            .moderatedAt(opp.getModeratedAt())
            .publishedAt(opp.getPublishedAt())
            .viewCount(opp.getViewCount())
            .saveCount(opp.getSaveCount())
            .applicationCount(opp.getApplicationCount())
            .organizationName(opp.getCreatedBy() != null ? opp.getCreatedBy().getOrgName() : null)
            .organizationId(opp.getCreatedBy() != null ? opp.getCreatedBy().getId() : null)
            .build();
    }
}
