package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.Permission;
import java.util.Optional;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    List<Permission> findByModule(String module);
}
