package tdop.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @Size(max = 200)
    private String fullName;
    @Size(max = 20)
    private String phone;
    @Size(max = 2000)
    private String bio;
    @Size(max = 200)
    private String location;
    private String profilePicture;
    @Size(max = 200)
    private String headline;
    @Size(max = 5000)
    private String summary;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String websiteUrl;
}
