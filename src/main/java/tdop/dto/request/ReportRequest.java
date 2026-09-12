package tdop.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ReportRequest {
    @NotBlank private String targetType;
    @NotNull private Long targetId;
    @NotBlank private String reason;
    private String description;
}
