package tdop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.storage")
public class FileStorageConfig {
    private String uploadDir = "uploads";
    private long maxFileSize = 10485760;
}
