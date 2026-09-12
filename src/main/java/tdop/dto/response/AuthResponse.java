package tdop.dto.response;
import lombok.*;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String email;
    private String fullName;
    private String role;
}
