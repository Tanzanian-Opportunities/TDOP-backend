package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import tdop.entity.enums.EscalationStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "escalations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escalation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private EscalationStatus status = EscalationStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_by")
    private User escalatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
