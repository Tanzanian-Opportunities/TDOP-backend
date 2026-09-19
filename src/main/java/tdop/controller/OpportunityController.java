package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Opportunity;
import tdop.entity.enums.UserRole;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import tdop.repository.UserRepository;
import tdop.service.OpportunityService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> browse() {
        return ResponseEntity.ok(opportunityService.getAllOpportunities());
    }

    @GetMapping("/search")
    public ResponseEntity<List<OpportunityResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(opportunityService.searchOpportunities(keyword));
    }

    @GetMapping("/filter/{category}")
    public ResponseEntity<List<OpportunityResponse>> filter(@PathVariable String category) {
        return ResponseEntity.ok(opportunityService.filterByCategory(category));
    }

    @GetMapping("/search/filtered")
    public ResponseEntity<List<OpportunityResponse>> searchFiltered(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(opportunityService.searchFiltered(category, type, location));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getById(@PathVariable Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setViewCount(opp.getViewCount() + 1);
        opportunityRepository.save(opp);
        return ResponseEntity.ok(opportunityService.toResponse(opp));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ORGANIZATION_ADMIN', 'ORGANIZATION_OWNER')")
    @PostMapping
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityRequest request,
                                                      @RequestParam Long orgId) {
        return ResponseEntity.ok(opportunityService.createOpportunity(request, orgId));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ORGANIZATION_ADMIN', 'ORGANIZATION_OWNER', 'ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody OpportunityRequest request,
                                                      Authentication auth) {
        checkOwnership(id, auth);
        return ResponseEntity.ok(opportunityService.updateOpportunity(id, request));
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ORGANIZATION_ADMIN', 'ORGANIZATION_OWNER', 'ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        checkOwnership(id, auth);
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ORGANIZATION_ADMIN', 'ORGANIZATION_OWNER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<OpportunityResponse> publish(@PathVariable Long id, Authentication auth) {
        checkOwnership(id, auth);
        return ResponseEntity.ok(opportunityService.publishOpportunity(id));
    }

    private void checkOwnership(Long oppId, Authentication auth) {
        var user = userRepository.findByEmail(auth.getName()).orElse(null);
        if (user == null) throw new ForbiddenException("User not found");
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPER_ADMIN) return;
        Opportunity opp = opportunityRepository.findById(oppId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        if (opp.getCreatedBy() == null) return;
        if (opp.getCreatedBy().getUser() != null && opp.getCreatedBy().getUser().getId().equals(user.getId())) return;
        throw new ForbiddenException("You can only manage your own organization's opportunities");
    }
}
