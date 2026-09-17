package tdop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InterestRequest {
    @NotBlank
    @Size(max = 100)
    private String category;
    @Size(max = 500)
    private String description;
}
