package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.service.CompareService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/compare")
@RequiredArgsConstructor
public class CompareController {

    private final CompareService compareService;

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> compare(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(compareService.compareOpportunities(ids));
    }
}
