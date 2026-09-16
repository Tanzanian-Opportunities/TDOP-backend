package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tdop.repository.OpportunityRepository;
import tdop.repository.OrganizationProfileRepository;
import tdop.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicStatsController {

    private final OrganizationProfileRepository organizationProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        YearMonth now = YearMonth.now();
        LocalDateTime startOfMonth = now.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = now.plusMonths(1).atDay(1).atStartOfDay();

        long totalOrganizations = organizationProfileRepository.count();
        long monthlyOpportunities = opportunityRepository.countPublishedThisMonth(startOfMonth, startOfNextMonth);
        long totalUsers = userRepository.count();

        Map<String, Object> stats = Map.of(
            "totalOrganizations", totalOrganizations,
            "monthlyOpportunities", monthlyOpportunities,
            "totalUsers", totalUsers
        );
        return ResponseEntity.ok(stats);
    }
}
