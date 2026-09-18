package tdop.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class OpportunityResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String type;
    private String category;
    private String salaryRange;
    private String status;
    private LocalDateTime deadline;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Extended fields
    private String sourceUrl;
    private String applicationUrl;
    private String workMode;
    private String educationLevel;
    private String experienceLevel;
    private String fundingInfo;
    private String requirements;
    private String benefits;
    private String eligibility;
    private String requiredDocuments;
    private boolean verified;
    private LocalDateTime verifiedAt;
    private boolean moderated;
    private LocalDateTime moderatedAt;
    private LocalDateTime publishedAt;
    private Long viewCount;
    private Long saveCount;
    private Long applicationCount;
    private String organizationName;
    private Long organizationId;
}
