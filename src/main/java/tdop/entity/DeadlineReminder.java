package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "deadline_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadlineReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @Column(nullable = false)
    private int reminderDays;

    @Column(nullable = false)
    private boolean sent = false;

    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
