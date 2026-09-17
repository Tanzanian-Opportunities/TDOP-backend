package tdop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tdop.entity.enums.SkillLevel;

@Data
public class SkillRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @Size(max = 100)
    private String category;
    private SkillLevel level;
}
