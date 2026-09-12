package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.SavedOpportunity;
import java.util.Optional;
import java.util.List;

@Repository
public interface SavedOpportunityRepository extends JpaRepository<SavedOpportunity, Long> {
    Optional<SavedOpportunity> findByUserIdAndOpportunityId(Long userId, Long opportunityId);
    List<SavedOpportunity> findByUserId(Long userId);
    void deleteByUserIdAndOpportunityId(Long userId, Long opportunityId);
}
