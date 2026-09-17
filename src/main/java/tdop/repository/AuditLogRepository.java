package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tdop.entity.AuditLog;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityId(Long entityId);

    List<AuditLog> findByEntityType(String entityType);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.entityType = :entityType")
    long countByEntityType(@Param("entityType") String entityType);
}
