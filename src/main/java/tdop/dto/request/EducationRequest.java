package tdop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EducationRequest {
    @NotBlank
    @Size(max = 200)
    private String institution;
    @NotBlank
    @Size(max = 200)
    private String degree;
    @Size(max = 200)
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
}
