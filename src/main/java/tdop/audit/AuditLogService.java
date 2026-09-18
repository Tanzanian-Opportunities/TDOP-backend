package tdop.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tdop.entity.AuditLog;
import tdop.repository.AuditLogRepository;
import tdop.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Around("@annotation(tdop.audit.Auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String action = joinPoint.getSignature().getName();
        String entityType = joinPoint.getTarget().getClass().getSimpleName();
        Long userId = getCurrentUserId();
        String ip = getClientIp();

        Object result = joinPoint.proceed();

        auditLogRepository.save(AuditLog.builder()
            .action(action)
            .entityType(entityType)
            .userId(userId)
            .ipAddress(ip)
            .timestamp(LocalDateTime.now())
            .build());

        return result;
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
            .ipAddress(ipAddress != null ? ipAddress : getClientIp())
            .timestamp(LocalDateTime.now())
            .build());
    }

    public void logAction(String action, String entityType, Long entityId, Long userId) {
        logAction(action, entityType, entityId, userId, null, null, null);
    }

    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                return userRepository.findByEmail(auth.getName()).map(u -> u.getId()).orElse(null);
            }
        } catch (Exception e) {
            log.debug("Could not resolve user ID for audit: {}", e.getMessage());
        }
        return null;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isEmpty()) {
                    return xff.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not resolve client IP: {}", e.getMessage());
        }
        return null;
    }
}
