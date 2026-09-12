package tdop.notification.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String orgName, String link) throws MessagingException {
        sendEmail(to, "Verify Your Account", EmailTemplate.buildVerificationEmail(orgName, link));
    }

    public void sendApplicationNotification(String to, String oppTitle, String applicantName) throws MessagingException {
        sendEmail(to, "New Application for " + oppTitle,
            EmailTemplate.buildApplicationEmail(oppTitle, applicantName));
    }
}
