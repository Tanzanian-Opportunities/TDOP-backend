package tdop.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.OrganizationInvitation;
import tdop.entity.OrganizationMember;
import tdop.entity.OrganizationProfile;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.OrganizationInvitationRepository;
import tdop.repository.OrganizationMemberRepository;
import tdop.repository.OrganizationProfileRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationProfileRepository orgProfileRepository;
    private final OrganizationMemberRepository memberRepository;
    private final OrganizationInvitationRepository invitationRepository;
    private final UserRepository userRepository;

    // --- Organization Profile ---

    public OrganizationProfile getProfile(Long orgId) {
        return orgProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    public OrganizationProfile getProfileByUserId(Long userId) {
        return orgProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization profile not found"));
    }

    public OrganizationProfile updateProfile(Long orgId, Long userId, String orgName, String description,
                                              String website, String industry, String size, String logo) {
        OrganizationProfile org = orgProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        assertOrganizationOwnerOrAdmin(org, userId);
        if (orgName != null) org.setOrgName(orgName);
        if (description != null) org.setDescription(description);
        if (website != null) org.setWebsite(website);
        if (industry != null) org.setIndustry(industry);
        if (size != null) org.setSize(size);
        if (logo != null) org.setLogo(logo);
        return orgProfileRepository.save(org);
    }

    public List<OrganizationProfile> getAllOrganizations() {
        return orgProfileRepository.findAll();
    }

    // --- Team Management ---

    public List<OrganizationMember> getMembers(Long orgId) {
        return memberRepository.findByOrganizationId(orgId);
    }

    public OrganizationMember getMember(Long orgId, Long userId) {
        return memberRepository.findByOrganizationIdAndUserId(orgId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
    }

    public boolean isMember(Long orgId, Long userId) {
        return memberRepository.existsByOrganizationIdAndUserId(orgId, userId);
    }

    public boolean isOwnerOrAdmin(Long orgId, Long userId) {
        return memberRepository.findByOrganizationIdAndUserId(orgId, userId)
            .map(m -> "OWNER".equals(m.getRole()) || "ADMIN".equals(m.getRole()))
            .orElse(false);
    }

    public void assertOrganizationOwnerOrAdmin(OrganizationProfile org, Long userId) {
        if (!org.getUser().getId().equals(userId) && !isOwnerOrAdmin(org.getId(), userId)) {
            throw new ForbiddenException("Not authorized to manage this organization");
        }
    }

    public OrganizationInvitation inviteMember(Long orgId, Long inviterUserId, String email, String role) {
        OrganizationProfile org = orgProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        assertOrganizationOwnerOrAdmin(org, inviterUserId);

        if (memberRepository.findByOrganizationIdAndUserId(orgId, 
            userRepository.findByEmail(email).map(User::getId).orElse(0L)).isPresent()) {
            throw new BadRequestException("User is already a member of this organization");
        }

        String token = UUID.randomUUID().toString();
        User inviter = userRepository.findById(inviterUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Inviter not found"));

        OrganizationInvitation invitation = OrganizationInvitation.builder()
            .organization(org)
            .email(email)
            .role(role != null ? role : "MEMBER")
            .token(token)
            .invitedBy(inviter)
            .status("PENDING")
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();
        return invitationRepository.save(invitation);
    }

    public OrganizationMember acceptInvitation(String token, Long userId) {
        OrganizationInvitation invitation = invitationRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new BadRequestException("Invitation is no longer pending");
        }
        if (LocalDateTime.now().isAfter(invitation.getExpiresAt())) {
            invitation.setStatus("EXPIRED");
            invitationRepository.save(invitation);
            throw new BadRequestException("Invitation has expired");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getEmail().equals(invitation.getEmail())) {
            throw new ForbiddenException("Invitation is for a different email address");
        }

        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);

        OrganizationMember member = OrganizationMember.builder()
            .organization(invitation.getOrganization())
            .user(user)
            .role(invitation.getRole())
            .status("ACTIVE")
            .invitedBy(invitation.getInvitedBy())
            .build();
        return memberRepository.save(member);
    }

    public void removeMember(Long orgId, Long memberUserId, Long removedByUserId) {
        OrganizationProfile org = orgProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        assertOrganizationOwnerOrAdmin(org, removedByUserId);

        OrganizationMember member = memberRepository.findByOrganizationIdAndUserId(orgId, memberUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if ("OWNER".equals(member.getRole())) {
            throw new BadRequestException("Cannot remove the organization owner");
        }

        member.setStatus("REMOVED");
        memberRepository.save(member);
    }

    public void updateMemberRole(Long orgId, Long memberUserId, String newRole, Long updatedByUserId) {
        OrganizationProfile org = orgProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        assertOrganizationOwnerOrAdmin(org, updatedByUserId);

        OrganizationMember member = memberRepository.findByOrganizationIdAndUserId(orgId, memberUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if ("OWNER".equals(member.getRole())) {
            throw new BadRequestException("Cannot change the owner's role");
        }

        member.setRole(newRole);
        memberRepository.save(member);
    }

    public List<OrganizationInvitation> getPendingInvitations(Long orgId) {
        return invitationRepository.findByOrganizationId(orgId).stream()
            .filter(i -> "PENDING".equals(i.getStatus()))
            .toList();
    }

    public void cancelInvitation(Long invitationId, Long userId) {
        OrganizationInvitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));
        assertOrganizationOwnerOrAdmin(invitation.getOrganization(), userId);
        invitation.setStatus("CANCELLED");
        invitationRepository.save(invitation);
    }
}
