package tdop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.ReportRequest;
import tdop.entity.Report;
import tdop.reporting.ReportService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> create(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(
            request.getReason(), Report.TargetType.valueOf(request.getTargetType().toUpperCase()),
            request.getTargetId(), request.getDescription()));
    }

    @GetMapping
    public ResponseEntity<List<Report>> list() {
        return ResponseEntity.ok(reportService.getReportsByStatus(tdop.entity.enums.ReportStatus.PENDING));
    }
}
