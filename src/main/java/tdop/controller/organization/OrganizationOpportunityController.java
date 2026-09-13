package tdop.controller.organization;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Application;
import tdop.service.CandidateService;
import tdop.service.OpportunityLifecycleService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/organization/opportunities")
@RequiredArgsConstructor
public class OrganizationOpportunityController {

    private final OpportunityLifecycleService opportunityLifecycleService;
    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> listByOrg(@RequestParam Long orgId) {
        return ResponseEntity.ok(opportunityLifecycleService.getOpportunitiesByOrg(orgId));
    }

    @PostMapping
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityRequest request,
                                                       @RequestParam Long orgId,
                                                       Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(opportunityLifecycleService.createDraft(request, orgId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody OpportunityRequest request,
                                                       Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(opportunityLifecycleService.updateOpportunity(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        opportunityLifecycleService.deleteOpportunity(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<OpportunityResponse> submit(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(opportunityLifecycleService.submitForReview(id, userId));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<OpportunityResponse> publish(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(opportunityLifecycleService.publish(id, userId));
    }

    @GetMapping("/{id}/applicants")
    public ResponseEntity<List<Application>> applicants(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(candidateService.getApplicantsForOpportunity(id, userId));
    }

    @PostMapping("/applications/{id}/shortlist")
    public ResponseEntity<Application> shortlist(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(candidateService.shortlistApplication(id, userId));
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<Application> rejectApplicant(@PathVariable Long id,
                                                        @RequestParam(required = false) String reason,
                                                        Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(candidateService.rejectApplication(id, userId, reason));
    }

    private Long getUserId(Authentication auth) {
        return null; // Will be handled by utility
    }
}
