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

    // Extended fields
    private String sourceUrl;
    private String applicationUrl;
    private String workMode;
    private String educationLevel;
    private String experienceLevel;
    private String fundingInfo;
    private boolean verified;
    private boolean moderated;
    private Long viewCount;
    private Long saveCount;
    private Long applicationCount;
}
