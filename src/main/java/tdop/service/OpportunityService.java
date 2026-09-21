package tdop.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Opportunity;
import tdop.entity.enums.OpportunityType;
import tdop.entity.OrganizationProfile;
import tdop.entity.enums.OpportunityStatus;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import tdop.repository.OrganizationProfileRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OrganizationProfileRepository organizationRepository;

    public OpportunityService(OpportunityRepository opportunityRepository,
                                OrganizationProfileRepository organizationRepository) {
        this.opportunityRepository = opportunityRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<OpportunityResponse> getAllOpportunities() {
        return opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OpportunityResponse> searchOpportunities(String keyword) {
        return opportunityRepository.searchPublished(keyword).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public OpportunityResponse getOpportunity(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        return toResponse(opp);
    }

    public OpportunityResponse createOpportunity(OpportunityRequest request, Long orgId) {
        OrganizationProfile org = organizationRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        Opportunity opp = Opportunity.builder()
            .title(request.getTitle()).description(request.getDescription())
            .requirements(request.getRequirements()).benefits(request.getBenefits())
            .location(request.getLocation()).type(OpportunityType.valueOf(request.getType().toUpperCase()))
            .category(request.getCategory()).salaryRange(request.getSalaryRange())
            .tags(request.getTags()).deadline(request.getDeadline())
            .status(OpportunityStatus.DRAFT).createdBy(org).build();
        return toResponse(opportunityRepository.save(opp));
    }

    public OpportunityResponse updateOpportunity(Long id, OpportunityRequest request) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setTitle(request.getTitle()); opp.setDescription(request.getDescription());
        opp.setRequirements(request.getRequirements()); opp.setBenefits(request.getBenefits());
        opp.setLocation(request.getLocation()); opp.setType(OpportunityType.valueOf(request.getType().toUpperCase()));
        opp.setCategory(request.getCategory()); opp.setSalaryRange(request.getSalaryRange());
        opp.setTags(request.getTags()); opp.setDeadline(request.getDeadline());
        return toResponse(opportunityRepository.save(opp));
    }

    public void deleteOpportunity(Long id) {
        opportunityRepository.deleteById(id);
    }

    public OpportunityResponse publishOpportunity(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setStatus(OpportunityStatus.PUBLISHED);
        return toResponse(opportunityRepository.save(opp));
    }

    public List<OpportunityResponse> filterByCategory(String category) {
        return opportunityRepository.findByCategoryAndStatus(category, OpportunityStatus.PUBLISHED).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OpportunityResponse> searchFiltered(String category, String type, String location) {
        return opportunityRepository.searchFiltered(category, type, location).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public OpportunityResponse toResponse(Opportunity opp) {
        return OpportunityResponse.builder().id(opp.getId()).title(opp.getTitle())
            .description(opp.getDescription()).location(opp.getLocation())
            .type(opp.getType().name()).category(opp.getCategory())
            .salaryRange(opp.getSalaryRange()).status(opp.getStatus().name())
            .deadline(opp.getDeadline()).tags(opp.getTags())
            .createdAt(opp.getCreatedAt()).build();
    }
}
