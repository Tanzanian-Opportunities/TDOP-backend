package tdop.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Notification;
import tdop.entity.enums.NotificationType;
import tdop.entity.User;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.NotificationRepository;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
            .user(User.builder().id(userId).build())
            .title(title)
            .message(message)
            .type(type)
            .read(false)
            .build();
        Notification saved = notificationRepository.save(notification);
        log.info("Notification created: userId={} type={} title='{}'", userId, type, title);
        return saved;
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!n.getUser().getId().equals(userId)) {
            throw new BadRequestException("Cannot mark another user's notification as read");
        }
        if (!n.isRead()) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }

    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }
}
