package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import tdop.entity.enums.VerificationStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationProfile organization;

    private String document;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status = VerificationStatus.PENDING;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
