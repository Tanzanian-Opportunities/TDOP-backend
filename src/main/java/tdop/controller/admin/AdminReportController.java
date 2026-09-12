package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdop.reporting.ReportService;
import tdop.entity.Report;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<List<Report>> list() {
        return ResponseEntity.ok(reportService.getReportsByStatus(tdop.entity.enums.ReportStatus.PENDING));
    }
}
