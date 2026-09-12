package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.SeekerProfile;
import java.util.Optional;

@Repository
public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Long> {
    Optional<SeekerProfile> findByUserId(Long userId);
}
