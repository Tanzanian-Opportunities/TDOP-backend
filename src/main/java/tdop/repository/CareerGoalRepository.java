package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.CareerGoal;
import java.util.List;

@Repository
public interface CareerGoalRepository extends JpaRepository<CareerGoal, Long> {
    List<CareerGoal> findByUserId(Long userId);
}
