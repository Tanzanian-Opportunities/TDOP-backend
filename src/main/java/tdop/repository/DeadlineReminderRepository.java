package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.DeadlineReminder;
import java.util.List;
import java.util.Optional;

public interface DeadlineReminderRepository extends JpaRepository<DeadlineReminder, Long> {
    List<DeadlineReminder> findByOpportunityId(Long opportunityId);
    Optional<DeadlineReminder> findByOpportunityIdAndReminderDays(Long opportunityId, int reminderDays);
    List<DeadlineReminder> findBySentFalse();
}
