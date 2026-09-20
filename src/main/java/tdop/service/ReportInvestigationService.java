package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Report;
import tdop.entity.User;
import tdop.entity.enums.ReportStatus;
import tdop.exception.BadRequestException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.ReportRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportInvestigationService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public Report createReport(Long reporterId, String targetType, Long targetId, String reason, String description) {
        User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));
        Report report = Report.builder()
            .reporter(reporter)
            .targetType(Report.TargetType.valueOf(targetType.toUpperCase()))
            .targetId(targetId)
            .reason(reason)
            .description(description)
            .status(ReportStatus.PENDING)
            .build();
        return reportRepository.save(report);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatusOrderByCreatedAtAsc(ReportStatus.PENDING);
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getReportsByType(String targetType) {
        return reportRepository.findByTargetType(targetType);
    }

    public List<Report> getReportsForTarget(String targetType, Long targetId) {
        return reportRepository.findByTargetTypeAndTargetId(targetType, targetId);
    }

    public Report assignToInvestigator(Long reportId, Long investigatorId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        User investigator = userRepository.findById(investigatorId)
            .orElseThrow(() -> new ResourceNotFoundException("Investigator not found"));
        LifecycleValidator.validateReportTransition(report.getStatus(), ReportStatus.REVIEWED);
        report.setAssignedTo(investigator);
        report.setStatus(ReportStatus.REVIEWED);
        return reportRepository.save(report);
    }

    public Report addInvestigationNotes(Long reportId, String notes) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setInvestigationNotes(notes);
        report.setReviewedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public Report resolveReport(Long reportId, String resolution) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        LifecycleValidator.validateReportTransition(report.getStatus(), ReportStatus.ACTIONED);
        report.setStatus(ReportStatus.ACTIONED);
        report.setResolution(resolution);
        report.setActionedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public Report dismissReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        LifecycleValidator.validateReportTransition(report.getStatus(), ReportStatus.ACTIONED);
        report.setStatus(ReportStatus.ACTIONED);
        report.setResolution("No action required");
        report.setActionedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public Map<String, Object> getReportAnalytics() {
        long total = reportRepository.count();
        long pending = reportRepository.countByStatus(ReportStatus.PENDING);
        long reviewed = reportRepository.countByStatus(ReportStatus.REVIEWED);
        long actioned = reportRepository.countByStatus(ReportStatus.ACTIONED);

        List<Object[]> byType = reportRepository.countByStatusGrouped();
        Map<String, Long> byTypeMap = byType.stream()
            .collect(Collectors.toMap(r -> r[0].toString(), r -> (Long) r[1]));

        return Map.of(
            "total", total,
            "pending", pending,
            "reviewed", reviewed,
            "actioned", actioned,
            "byStatus", byTypeMap
        );
    }

    public long countPending() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }
}
