package tdop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tdop.service.ReportInvestigationService;
import tdop.entity.Report;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MODERATOR')")
public class AdminReportController {

    private final ReportInvestigationService reportService;

    @GetMapping
    public ResponseEntity<List<Report>> list() {
        return ResponseEntity.ok(reportService.getPendingReports());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Report>> listAll() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(reportService.getReportAnalytics());
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Report> assign(@PathVariable Long id, @RequestParam Long investigatorId) {
        return ResponseEntity.ok(reportService.assignToInvestigator(id, investigatorId));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Report> resolve(@PathVariable Long id, @RequestParam String resolution) {
        return ResponseEntity.ok(reportService.resolveReport(id, resolution));
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<Report> dismiss(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.dismissReport(id));
    }

    @PutMapping("/{id}/notes")
    public ResponseEntity<Report> addNotes(@PathVariable Long id, @RequestParam String notes) {
        return ResponseEntity.ok(reportService.addInvestigationNotes(id, notes));
    }
}
