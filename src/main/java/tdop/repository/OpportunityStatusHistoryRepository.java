package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.OpportunityStatusHistory;
import java.util.List;

public interface OpportunityStatusHistoryRepository extends JpaRepository<OpportunityStatusHistory, Long> {
    List<OpportunityStatusHistory> findByOpportunityIdOrderByCreatedAtDesc(Long opportunityId);
}
