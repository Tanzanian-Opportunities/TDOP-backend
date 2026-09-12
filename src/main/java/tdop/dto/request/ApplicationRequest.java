package tdop.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ApplicationRequest {
    private String coverLetter;
    private String resumeUrl;
}
