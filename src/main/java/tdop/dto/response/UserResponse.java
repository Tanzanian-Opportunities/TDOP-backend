package tdop.dto.response;
import lombok.*;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private boolean enabled;
    private boolean verified;
}
