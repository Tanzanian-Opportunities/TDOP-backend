package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tdop.entity.RiskSignal;
import java.util.List;

@Repository
public interface RiskSignalRepository extends JpaRepository<RiskSignal, Long> {
    List<RiskSignal> findByTargetTypeAndTargetId(String targetType, Long targetId);
    List<RiskSignal> findByReviewedFalse();
    List<RiskSignal> findByRiskLevel(String riskLevel);
    long countByRiskLevel(String riskLevel);
    long countByTargetTypeAndTargetId(String targetType, Long targetId);

    @Query("SELECT COUNT(r) FROM RiskSignal r WHERE r.reviewed = false")
    long countUnreviewed();
}
