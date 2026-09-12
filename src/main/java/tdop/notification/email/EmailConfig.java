package tdop.notification.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender(EmailProperties emailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailProperties.getHost());
        mailSender.setPort(emailProperties.getPort());
        mailSender.setUsername(emailProperties.getUsername());
        mailSender.setPassword(emailProperties.getPassword());

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", emailProperties.getProperties() != null
                && emailProperties.getProperties().getSmtp() != null
                && emailProperties.getProperties().getSmtp().isAuth());
        properties.put("mail.smtp.starttls.enable", emailProperties.getProperties() != null
                && emailProperties.getProperties().getSmtp() != null
                && emailProperties.getProperties().getSmtp().getStarttls() != null
                && emailProperties.getProperties().getSmtp().getStarttls().isEnable());
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }
}