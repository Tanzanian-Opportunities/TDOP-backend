package tdop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
public class OpportunityRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    private String requirements;
    private String benefits;
    private String location;
    private String type;
    private String category;
    private String salaryRange;
    private String tags;
    @NotNull private LocalDateTime deadline;

    // Extended fields for Developer 02
    private String sourceUrl;
    private String applicationUrl;
    private String workMode;
    private String educationLevel;
    private String experienceLevel;
    private String fundingInfo;
    private String eligibility;
    private String requiredDocuments;
}
