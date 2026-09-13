package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.OrganizationProfile;
import tdop.organization.OrganizationService;
import tdop.service.VerificationOfficerService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/organizations")
@RequiredArgsConstructor
public class AdminOrganizationController {

    private final OrganizationService organizationService;
    private final VerificationOfficerService verificationService;

    @GetMapping
    public ResponseEntity<List<OrganizationProfile>> list() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationProfile> get(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getProfile(id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<?> members(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getMembers(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(Map.of(
            "totalOrganizations", organizationService.getAllOrganizations().size(),
            "pendingVerifications", verificationService.countPending()
        ));
    }
}
