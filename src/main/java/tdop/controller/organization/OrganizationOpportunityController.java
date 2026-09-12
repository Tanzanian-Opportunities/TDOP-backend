package tdop.controller.organization;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.OpportunityRequest;
import tdop.dto.response.OpportunityResponse;
import tdop.service.OpportunityService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/organization/opportunities")
@RequiredArgsConstructor
public class OrganizationOpportunityController {

    private final OpportunityService opportunityService;

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> list() {
        return ResponseEntity.ok(opportunityService.getAllOpportunities());
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
        return ResponseEntity.ok().build();
    }
}
