package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.Escalation;
import tdop.entity.enums.EscalationStatus;
import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {
    List<Escalation> findByStatusOrderByCreatedAtDesc(EscalationStatus status);
    long countByStatus(EscalationStatus status);
    List<Escalation> findByTargetTypeAndTargetId(String targetType, Long targetId);
    List<Escalation> findByAssignedToId(Long userId);
}
