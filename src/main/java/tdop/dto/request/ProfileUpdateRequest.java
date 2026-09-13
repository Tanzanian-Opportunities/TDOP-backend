package tdop.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String bio;
    private String location;
    private String profilePicture;
    private String headline;
    private String summary;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String websiteUrl;
}
