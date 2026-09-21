package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.entity.OrganizationMember;
import tdop.entity.OrganizationInvitation;
import tdop.exception.ForbiddenException;
import tdop.organization.OrganizationService;
import tdop.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/organization/team")
@RequiredArgsConstructor
public class OrganizationTeamController {

    private final OrganizationService organizationService;
    private final UserService userService;

    @GetMapping("/{orgId}/members")
    public ResponseEntity<List<OrganizationMember>> members(@PathVariable Long orgId, Authentication auth) {
        Long userId = getUserId(auth);
        organizationService.assertOrganizationOwnerOrAdmin(organizationService.getProfile(orgId), userId);
        return ResponseEntity.ok(organizationService.getMembers(orgId));
    }

    @PostMapping("/{orgId}/invite")
    public ResponseEntity<OrganizationInvitation> invite(@PathVariable Long orgId,
                                                         @RequestParam String email,
                                                         @RequestParam(required = false, defaultValue = "MEMBER") String role,
                                                         Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(organizationService.inviteMember(orgId, userId, email, role));
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<OrganizationMember> acceptInvitation(@RequestParam String token, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(organizationService.acceptInvitation(token, userId));
    }

    @DeleteMapping("/{orgId}/members/{memberUserId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long orgId,
                                              @PathVariable Long memberUserId,
                                              Authentication auth) {
        Long userId = getUserId(auth);
        organizationService.removeMember(orgId, memberUserId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orgId}/members/{memberUserId}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Long orgId,
                                            @PathVariable Long memberUserId,
                                            @RequestParam String role,
                                            Authentication auth) {
        Long userId = getUserId(auth);
        organizationService.updateMemberRole(orgId, memberUserId, role, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orgId}/invitations")
    public ResponseEntity<List<OrganizationInvitation>> invitations(@PathVariable Long orgId, Authentication auth) {
        Long userId = getUserId(auth);
        organizationService.assertOrganizationOwnerOrAdmin(organizationService.getProfile(orgId), userId);
        return ResponseEntity.ok(organizationService.getPendingInvitations(orgId));
    }

    private Long getUserId(Authentication auth) {
        return userService.getUserIdByEmail(auth.getName());
    }
}
