package tdop.rbac;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Permission;
import tdop.entity.Role;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.PermissionRepository;
import tdop.repository.RoleRepository;
import tdop.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RbacService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name));
    }

    public Role createRole(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Role already exists: " + name);
        }
        Role role = Role.builder()
            .name(name)
            .description(description)
            .isSystem(false)
            .build();
        return roleRepository.save(role);
    }

    public void assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.getPlatformRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.getPlatformRoles().remove(role);
        userRepository.save(user);
    }

    public Set<String> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getPlatformRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .collect(Collectors.toSet());
    }

    public boolean hasPermission(Long userId, String permissionName) {
        return getUserPermissions(userId).contains(permissionName);
    }

    public boolean hasAnyRole(Long userId, String... roleNames) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Set<String> userRoleNames = user.getPlatformRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        for (String roleName : roleNames) {
            if (userRoleNames.contains(roleName)) return true;
        }
        return false;
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<Permission> getPermissionsByModule(String module) {
        return permissionRepository.findByModule(module);
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        if (role.isSystem()) {
            throw new BadRequestException("Cannot delete system role");
        }
        roleRepository.delete(role);
    }
}
