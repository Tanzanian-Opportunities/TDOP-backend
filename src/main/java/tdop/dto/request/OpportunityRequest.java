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
}
