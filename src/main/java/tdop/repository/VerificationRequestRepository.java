package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.VerificationRequest;
import java.util.Optional;

@Repository
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {
    Optional<VerificationRequest> findByOrganizationId(Long orgId);
}
