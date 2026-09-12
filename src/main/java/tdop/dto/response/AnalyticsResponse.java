package tdop.dto.response;
import lombok.*;
import java.util.Map;

@Data
@Builder
public class AnalyticsResponse {
    private Map<String, Object> stats;
    private Map<String, Object> chartData;
}
