package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tdop.entity.Opportunity;
import tdop.entity.enums.OpportunityStatus;
import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findByCreatedById(Long orgId);
    List<Opportunity> findByStatus(OpportunityStatus status);
    List<Opportunity> findByCategoryAndStatus(String category, OpportunityStatus status);
    List<Opportunity> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT o FROM Opportunity o WHERE o.status = 'PUBLISHED'")
    List<Opportunity> findPublished();
}
