package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.DeadlineReminder;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeadlineReminderRepository extends JpaRepository<DeadlineReminder, Long> {
    Optional<DeadlineReminder> findByOpportunityIdAndReminderDays(Long opportunityId, int reminderDays);
    List<DeadlineReminder> findByOpportunityId(Long opportunityId);
}