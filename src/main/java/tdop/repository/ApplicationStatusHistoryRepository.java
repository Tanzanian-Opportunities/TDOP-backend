package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.ApplicationStatusHistory;
import java.util.List;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {
    List<ApplicationStatusHistory> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}
