package tdop.rbac;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tdop.entity.User;
import tdop.repository.UserRepository;
import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class PermissionEvaluatorImpl implements PermissionEvaluator {

    private final UserRepository userRepository;
    private final RbacService rbacService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;
        return rbacService.hasPermission(user.getId(), permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;
        return rbacService.hasPermission(user.getId(), permission.toString());
    }
}
