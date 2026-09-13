package tdop.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tdop.entity.AuditLog;
import tdop.repository.AuditLogRepository;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(pointcut = "execution(* tdop.controller..*.*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint) {
        String action = joinPoint.getSignature().getName();
        String entityType = joinPoint.getTarget().getClass().getSimpleName();
        Long userId = getCurrentUserId();
        auditLogRepository.save(AuditLog.builder()
            .action(action)
            .entityType(entityType)
            .userId(userId)
            .timestamp(LocalDateTime.now())
            .build());
    }

    public void logAction(String action, String entityType, Long entityId, Long userId,
                           String oldValue, String newValue, String ipAddress) {
        auditLogRepository.save(AuditLog.builder()
            .action(action)
            .entityType(entityType)
            .entityId(entityId)
            .userId(userId)
            .oldValue(oldValue)
            .newValue(newValue)
            .ipAddress(ipAddress)
            .timestamp(LocalDateTime.now())
            .build());
    }

    public void logSensitiveAction(String action, String entityType, Long entityId, Long userId,
                                    String oldValue, String newValue, String ipAddress) {
        logAction(action, entityType, entityId, userId, oldValue, newValue, ipAddress);
    }

    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                return null; // Would need to look up user ID from email
            }
        } catch (Exception e) {
            // Silently fail for audit logging
        }
        return null;
    }
}
