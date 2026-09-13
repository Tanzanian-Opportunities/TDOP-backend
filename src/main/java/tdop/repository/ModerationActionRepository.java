package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.ModerationAction;
import java.util.List;

public interface ModerationActionRepository extends JpaRepository<ModerationAction, Long> {
    List<ModerationAction> findByOpportunityIdOrderByCreatedAtDesc(Long opportunityId);
    List<ModerationAction> findByModeratorId(Long moderatorId);
}
