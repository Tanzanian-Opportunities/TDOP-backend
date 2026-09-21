package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
