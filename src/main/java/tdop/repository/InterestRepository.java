package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.Interest;
import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByUserId(Long userId);
}
