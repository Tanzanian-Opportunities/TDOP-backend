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
    private java.time.LocalDateTime deadline;
    private String tags;
    private java.time.LocalDateTime createdAt;
}
