package tdop.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ApplicationRequest {
    @Size(max = 5000)
    private String coverLetter;
    private String resumeUrl;
}
