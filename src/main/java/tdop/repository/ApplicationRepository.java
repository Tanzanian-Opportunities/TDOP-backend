package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tdop.entity.Application;
import tdop.entity.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByOpportunityId(Long opportunityId);
    List<Application> findByApplicantId(Long applicantId);
    List<Application> findByStatus(ApplicationStatus status);
    long countByStatus(ApplicationStatus status);
    long countByOpportunityIdAndStatus(Long opportunityId, ApplicationStatus status);
    long countByOpportunityIdAndShortlisted(Long opportunityId, boolean shortlisted);
    boolean existsByApplicantIdAndOpportunityId(Long applicantId, Long opportunityId);
    Optional<Application> findByApplicantIdAndOpportunityId(Long applicantId, Long opportunityId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.appliedAt >= :since")
    long countCreatedSince(@Param("since") LocalDateTime since);
}
