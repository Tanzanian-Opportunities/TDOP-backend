package tdop.dto.request;

import lombok.Data;

@Data
public class EducationRequest {
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
}
