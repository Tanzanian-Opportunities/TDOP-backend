package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.OrganizationProfile;
import java.util.Optional;

@Repository
public interface OrganizationProfileRepository extends JpaRepository<OrganizationProfile, Long> {
    Optional<OrganizationProfile> findByUserId(Long userId);
}
