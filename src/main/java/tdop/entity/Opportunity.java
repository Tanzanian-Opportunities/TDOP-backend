package tdop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tdop.entity.enums.OpportunityStatus;
import tdop.entity.enums.OpportunityType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opportunities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    private String location;

    @Enumerated(EnumType.STRING)
    private OpportunityType type;

    private String category;
    private String salaryRange;
    private String tags;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus status = OpportunityStatus.DRAFT;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private OrganizationProfile createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    // Extended fields for Developer 02
    private String sourceUrl;
    private String applicationUrl;

    private String workMode = "ONSITE";

    private String educationLevel;
    private String experienceLevel;
    private String fundingInfo;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    @Column(columnDefinition = "TEXT")
    private String requiredDocuments;

    private boolean verified = false;
    private LocalDateTime verifiedAt;
    private String verifiedBy;

    private boolean moderated = false;
    private LocalDateTime moderatedAt;
    private String moderatedBy;

    private LocalDateTime publishedAt;

    private boolean closingSoonNotified = false;

    private String riskScore = "LOW";

    private Long viewCount = 0L;
    private Long saveCount = 0L;
    private Long applicationCount = 0L;

    @JsonIgnore
    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OpportunityStatusHistory> statusHistory = new ArrayList<>();
}
