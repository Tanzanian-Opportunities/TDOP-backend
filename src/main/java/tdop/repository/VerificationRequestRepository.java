package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tdop.entity.VerificationRequest;
import tdop.entity.enums.VerificationStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {
    Optional<VerificationRequest> findByOrganizationId(Long orgId);
    List<VerificationRequest> findByStatus(VerificationStatus status);
    long countByStatus(VerificationStatus status);
    List<VerificationRequest> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
}
