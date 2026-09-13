package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_request_id", nullable = false)
    private VerificationRequest verificationRequest;

    @Column(nullable = false)
    private String documentUrl;

    private String documentType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
}
