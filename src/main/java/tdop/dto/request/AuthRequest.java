package tdop.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class AuthRequest {
    @NotBlank @Email private String email;
}
