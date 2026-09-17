package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_signals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskSignal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String signalType;

    @Column(nullable = false)
    private String riskLevel = "LOW";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean autoDetected = false;

    @Column(nullable = false)
    private boolean reviewed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }

    public enum SignalType {
        DUPLICATE_OPPORTUNITY, SUSPICIOUS_URL, REPEATED_REPORTS,
        UNVERIFIED_ORG, UNUSUAL_PATTERNS, REJECTED_OPPORTUNITIES,
        DEADLINE_ANOMALY, SOURCE_QUALITY, SUSPICIOUS_APPLICATION_DEST
    }
}
