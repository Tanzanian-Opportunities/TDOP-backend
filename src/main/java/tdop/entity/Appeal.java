package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import tdop.entity.enums.AppealStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "appeals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appeal {
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
    private AppealStatus status = AppealStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appellant_id")
    private User appellant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String reviewNotes;

    private String resolution;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
