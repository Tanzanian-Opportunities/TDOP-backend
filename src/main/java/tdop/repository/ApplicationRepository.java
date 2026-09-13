package tdop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.Application;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByOpportunityId(Long opportunityId);
    List<Application> findByApplicantId(Long applicantId);
    Optional<Application> findByApplicantIdAndOpportunityId(Long applicantId, Long opportunityId);
}
