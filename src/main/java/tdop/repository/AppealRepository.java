package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.Appeal;
import tdop.entity.enums.AppealStatus;
import java.util.List;

@Repository
public interface AppealRepository extends JpaRepository<Appeal, Long> {
    List<Appeal> findByStatusOrderByCreatedAtDesc(AppealStatus status);
    long countByStatus(AppealStatus status);
    List<Appeal> findByTargetTypeAndTargetId(String targetType, Long targetId);
    List<Appeal> findByAppellantId(Long userId);
}
