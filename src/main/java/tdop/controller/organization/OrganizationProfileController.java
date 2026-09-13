package tdop.controller.organization;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.OrganizationProfile;
import tdop.organization.OrganizationService;

@RestController
@RequestMapping("/api/v1/organization/profile")
@RequiredArgsConstructor
public class OrganizationProfileController {

    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<OrganizationProfile> view(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(organizationService.getProfileByUserId(userId));
    }

    @PutMapping
    public ResponseEntity<OrganizationProfile> update(Authentication auth,
                                                       @RequestParam Long orgId,
                                                       @RequestParam(required = false) String orgName,
                                                       @RequestParam(required = false) String description,
                                                       @RequestParam(required = false) String website,
                                                       @RequestParam(required = false) String industry,
                                                       @RequestParam(required = false) String size,
                                                       @RequestParam(required = false) String logo) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(organizationService.updateProfile(orgId, userId, orgName, description, website, industry, size, logo));
    }

    @GetMapping("/all")
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    private Long getUserId(Authentication auth) {
        // Extract user ID from authentication principal
        // This is a simplified approach - in production, extract from JWT or lookup by email
        return null; // Will be handled by a utility method
    }
}
