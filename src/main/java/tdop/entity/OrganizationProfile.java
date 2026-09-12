package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String orgName;

    private String description;
    private String website;
    private String industry;
    private String size;
    private String logo;
    private boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    private String verificationDocument;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Opportunity> opportunities = new ArrayList<>();
}
