package tdop.notification.email;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mail")
public class EmailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private MailProperties properties;

    @Data
    public static class MailProperties {
        private MailSmtp smtp;
    }

    @Data
    public static class MailSmtp {
        private boolean auth;
        private MailStarttls starttls;
    }

    @Data
    public static class MailStarttls {
        private boolean enable;
    }
}
