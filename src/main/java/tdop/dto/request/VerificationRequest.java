package tdop.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class VerificationRequest {
    @NotBlank private String document;
}
