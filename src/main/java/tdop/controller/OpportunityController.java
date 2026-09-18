package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.entity.Opportunity;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OpportunityRepository;
import tdop.service.OpportunityService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final OpportunityRepository opportunityRepository;

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

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getById(@PathVariable Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        opp.setViewCount(opp.getViewCount() + 1);
        opportunityRepository.save(opp);
        return ResponseEntity.ok(opportunityService.toResponse(opp));
    }

    @PostMapping
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityRequest request,
                                                      @RequestParam Long orgId) {
        return ResponseEntity.ok(opportunityService.createOpportunity(request, orgId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody OpportunityRequest request) {
        return ResponseEntity.ok(opportunityService.updateOpportunity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<OpportunityResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.publishOpportunity(id));
    }
}
