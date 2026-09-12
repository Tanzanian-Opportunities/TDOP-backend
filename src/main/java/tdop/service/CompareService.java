package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tdop.entity.Opportunity;
import tdop.repository.OpportunityRepository;
import tdop.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompareService {

    private final OpportunityRepository opportunityRepository;

    public CompareService(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
    }

    public List<Map<String, Object>> compareOpportunities(List<Long> ids) {
        return ids.stream().map(id -> {
            Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
            return Map.<String, Object>of("id", opp.getId(), "title", opp.getTitle(),
                "description", opp.getDescription(), "location", opp.getLocation(),
                "type", opp.getType().name(), "category", opp.getCategory(),
                "salaryRange", opp.getSalaryRange(), "status", opp.getStatus().name(),
                "deadline", opp.getDeadline(), "tags", opp.getTags());
        }).collect(Collectors.toList());
    }
}
