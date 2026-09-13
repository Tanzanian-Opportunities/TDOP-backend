package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import tdop.entity.enums.ReportStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    @Column(nullable = false)
    private String reason;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(columnDefinition = "TEXT")
    private String investigationNotes;

    private String resolution;

    private LocalDateTime reviewedAt;

    private LocalDateTime actionedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TargetType { OPPORTUNITY, USER, ORGANIZATION }

    public enum ReportReason {
        SCAM, FAKE_ORGANIZATION, SUSPICIOUS_LINK, MISLEADING_INFORMATION,
        DUPLICATE, EXPIRED, WRONG_ELIGIBILITY, INAPPROPRIATE_CONTENT, OTHER
    }
}
