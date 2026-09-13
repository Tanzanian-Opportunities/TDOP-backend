package tdop.dto.request;

import lombok.Data;
import tdop.entity.enums.SkillLevel;

@Data
public class SkillRequest {
    private String name;
    private String category;
    private SkillLevel level;
}
