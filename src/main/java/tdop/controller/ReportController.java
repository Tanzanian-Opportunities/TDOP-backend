package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.ReportRequest;
import tdop.entity.Report;
import tdop.service.ReportInvestigationService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportInvestigationService reportService;

    @PostMapping
    public ResponseEntity<Report> create(@Valid @RequestBody ReportRequest request, Authentication auth) {
        Long reporterId = getUserId(auth);
        return ResponseEntity.ok(reportService.createReport(reporterId,
            request.getTargetType(), request.getTargetId(),
            request.getReason(), request.getDescription()));
    }

    @GetMapping
    public ResponseEntity<List<Report>> list() {
        return ResponseEntity.ok(reportService.getPendingReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> get(@PathVariable Long id) {
        // Would need a get by ID method
        return ResponseEntity.ok().build();
    }

    private Long getUserId(Authentication auth) {
        return null;
    }
}
