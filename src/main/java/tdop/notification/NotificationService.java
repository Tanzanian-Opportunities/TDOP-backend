package tdop.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Notification;
import tdop.entity.enums.NotificationType;
import tdop.entity.User;
import tdop.repository.NotificationRepository;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
            .user(User.builder().id(userId).build()).title(title)
            .message(message).type(type).read(false).build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead(Long userId) {
        java.util.List<Notification> notifications = notificationRepository.findByUserId(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}
