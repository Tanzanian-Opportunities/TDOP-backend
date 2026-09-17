package tdop.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private long savedCount;
    private long applicationCount;
    private long unreadNotificationCount;
    private double profileCompletion;
    private long availableOpportunities;
    private List<Map<String, Object>> upcomingDeadlines;
    private List<Map<String, Object>> recentApplications;
    private Map<String, Long> applicationStats;
    private Map<String, Object> applicationReadiness;
}
