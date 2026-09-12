package tdop.reporting;

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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public Report createReport(String reason, Report.TargetType targetType, Long targetId, String description) {
        User reporter = userRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("No user found"));
        Report report = Report.builder()
            .reporter(reporter).targetType(targetType).targetId(targetId)
            .reason(reason).description(description).status(ReportStatus.PENDING)
            .build();
        return reportRepository.save(report);
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    public List<Map<String, Object>> getAnalytics() {
        List<Object[]> results = reportRepository.countByStatusGrouped();
        return results.stream().map(r -> Map.of("status", r[0], "count", r[1])).collect(Collectors.toList());
    }

    public void reviewReport(Long id, ReportStatus status) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(status);
        reportRepository.save(report);
    }
}
