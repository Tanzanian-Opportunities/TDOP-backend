package tdop.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import tdop.entity.AuditLog;
import tdop.repository.AuditLogRepository;
import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning(pointcut = "execution(* tdop.controller..*.*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint) {
        String action = joinPoint.getSignature().getName();
        String entityType = joinPoint.getTarget().getClass().getSimpleName();
        auditLogRepository.save(AuditLog.builder()
            .action(action)
            .entityType(entityType)
            .timestamp(LocalDateTime.now())
            .build());
    }
}
