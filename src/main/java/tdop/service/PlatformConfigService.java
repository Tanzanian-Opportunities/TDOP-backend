package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.PlatformConfig;
import tdop.entity.User;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.PlatformConfigRepository;
import tdop.repository.UserRepository;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlatformConfigService {

    private final PlatformConfigRepository configRepository;
    private final UserRepository userRepository;

    public List<PlatformConfig> getAllConfig() {
        return configRepository.findAll();
    }

    public PlatformConfig getConfig(String key) {
        return configRepository.findByConfigKey(key)
            .orElseThrow(() -> new ResourceNotFoundException("Config not found: " + key));
    }

    public String getConfigValue(String key, String defaultValue) {
        return configRepository.findByConfigKey(key)
            .map(PlatformConfig::getConfigValue)
            .orElse(defaultValue);
    }

    public PlatformConfig setConfig(String key, String value, String description, Long updatedByUserId) {
        PlatformConfig config = configRepository.findByConfigKey(key)
            .orElse(PlatformConfig.builder()
                .configKey(key)
                .build());
        config.setConfigValue(value);
        if (description != null) config.setDescription(description);
        if (updatedByUserId != null) {
            config.setUpdatedBy(userRepository.findById(updatedByUserId).orElse(null));
        }
        return configRepository.save(config);
    }

    public void deleteConfig(String key) {
        PlatformConfig config = configRepository.findByConfigKey(key)
            .orElseThrow(() -> new ResourceNotFoundException("Config not found: " + key));
        configRepository.delete(config);
    }
}
