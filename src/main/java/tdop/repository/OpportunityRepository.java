package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Opportunity> findByStatusIn(List<OpportunityStatus> statuses);

    long countByStatus(OpportunityStatus status);

    long countByCreatedByUserId(Long userId);

    @Query("SELECT o FROM Opportunity o WHERE o.status = 'PUBLISHED' AND o.deadline > CURRENT_TIMESTAMP ORDER BY o.createdAt DESC")
    List<Opportunity> findActivePublished();

    @Query("SELECT o FROM Opportunity o WHERE o.status IN ('SUBMITTED', 'UNDER_REVIEW')")
    List<Opportunity> findPendingModeration();

    @Query("SELECT o FROM Opportunity o WHERE o.status IN ('PUBLISHED', 'CLOSING_SOON') AND o.deadline <= CURRENT_TIMESTAMP AND o.status != 'EXPIRED'")
    List<Opportunity> findExpiredNotMarked();

    @Query(value = "SELECT * FROM opportunities o WHERE o.status = 'PUBLISHED' AND o.deadline > CURRENT_TIMESTAMP AND o.deadline <= CURRENT_TIMESTAMP + INTERVAL '7 days'", nativeQuery = true)
    List<Opportunity> findClosingSoon();

    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.publishedAt >= :startOfMonth AND o.publishedAt < :startOfNextMonth")
    long countPublishedThisMonth(@Param("startOfMonth") java.time.LocalDateTime startOfMonth, @Param("startOfNextMonth") java.time.LocalDateTime startOfNextMonth);

    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.createdAt >= :since")
    long countCreatedSince(@Param("since") java.time.LocalDateTime since);
}
