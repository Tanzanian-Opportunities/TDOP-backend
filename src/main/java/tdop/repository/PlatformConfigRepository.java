package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.PlatformConfig;
import java.util.Optional;

public interface PlatformConfigRepository extends JpaRepository<PlatformConfig, Long> {
    Optional<PlatformConfig> findByConfigKey(String configKey);
}
