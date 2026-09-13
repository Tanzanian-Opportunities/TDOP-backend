package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.OrganizationInvitation;
import java.util.List;
import java.util.Optional;

public interface OrganizationInvitationRepository extends JpaRepository<OrganizationInvitation, Long> {
    List<OrganizationInvitation> findByOrganizationId(Long organizationId);
    Optional<OrganizationInvitation> findByToken(String token);
    Optional<OrganizationInvitation> findByEmailAndOrganizationIdAndStatus(String email, Long organizationId, String status);
    List<OrganizationInvitation> findByStatus(String status);
}
