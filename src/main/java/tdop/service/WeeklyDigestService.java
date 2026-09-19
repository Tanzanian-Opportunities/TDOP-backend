package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.SeekerProfile;
import tdop.entity.User;
import tdop.entity.enums.NotificationPreference;
import tdop.entity.enums.OpportunityStatus;
import tdop.notification.email.EmailService;
import tdop.repository.OpportunityRepository;
import tdop.repository.SeekerProfileRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeeklyDigestService {

    private final SeekerProfileRepository seekerProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RecommendationService recommendationService;

    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyDigests() {
        List<SeekerProfile> profiles = seekerProfileRepository.findAll();
        int sent = 0;
        for (SeekerProfile profile : profiles) {
            if (profile.getUser() == null) continue;
            if (profile.getNotificationPreference() == NotificationPreference.NONE) continue;

            try {
                List<?> recommendations = recommendationService.getRecommendations(
                    profile.getUser().getId(), 5);
                if (recommendations.isEmpty()) continue;

                String email = profile.getUser().getEmail();
                String name = profile.getUser().getFullName();
                String body = buildDigestHtml(name, recommendations.size());
                emailService.sendEmail(email, "TDOP Weekly: New Opportunities for You", body);
                sent++;
            } catch (Exception e) {
                log.warn("Failed to send weekly digest to {}: {}",
                    profile.getUser().getEmail(), e.getMessage());
            }
        }
        log.info("Weekly digest sent to {} seekers", sent);
    }

    private String buildDigestHtml(String name, int count) {
        return "<h2>Weekly Opportunity Digest</h2>"
            + "<p>Hi " + name + ",</p>"
            + "<p>We found <strong>" + count + " new opportunities</strong> that match your profile.</p>"
            + "<p>Log in to TDOP to view your personalized recommendations.</p>"
            + "<p><a href=\"" + getFrontendUrl() + "/opportunities\">Browse Opportunities</a></p>"
            + "<br><p>Best regards,<br>The TDOP Team</p>";
    }

    private String getFrontendUrl() {
        String url = System.getenv("FRONTEND_URL");
        return url != null ? url : "https://tdop.co.tz";
    }
}
