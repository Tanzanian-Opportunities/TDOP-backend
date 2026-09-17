package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.dto.response.DashboardResponse;
import tdop.entity.Application;
import tdop.entity.Opportunity;
import tdop.entity.Skill;
import tdop.entity.Education;
import tdop.entity.Experience;
import tdop.entity.UserDocument;
import tdop.entity.enums.ApplicationStatus;
import tdop.entity.enums.OpportunityStatus;
import tdop.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {

    private final SavedOpportunityRepository savedRepository;
    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final NotificationRepository notificationRepository;
    private final SeekerProfileService seekerProfileService;
    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final UserDocumentRepository documentRepository;

    public DashboardResponse getDashboardData(Long userId) {
        long savedCount = savedRepository.findByUserId(userId).size();
        long applicationCount = applicationRepository.findByApplicantId(userId).size();
        long unreadCount = notificationRepository.findByUserId(userId).stream()
            .filter(n -> !n.isRead()).count();
        double profileCompletion = seekerProfileService.calculateProfileCompletion(userId);

        long availableOpps = opportunityRepository.findPublished().size();

        List<Map<String, Object>> upcomingDeadlines = getUpcomingDeadlines(userId);
        List<Map<String, Object>> recentApplications = getRecentApplications(userId);
        Map<String, Long> applicationStats = getApplicationStats(userId);
        Map<String, Object> applicationReadiness = getApplicationReadiness(userId);

        return DashboardResponse.builder()
            .savedCount(savedCount)
            .applicationCount(applicationCount)
            .unreadNotificationCount(unreadCount)
            .profileCompletion(profileCompletion)
            .availableOpportunities(availableOpps)
            .upcomingDeadlines(upcomingDeadlines)
            .recentApplications(recentApplications)
            .applicationStats(applicationStats)
            .applicationReadiness(applicationReadiness)
            .build();
    }

    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
            .filter(n -> !n.isRead()).count();
    }

    public List<Map<String, Object>> getUpcomingDeadlines(Long userId) {
        Set<Long> savedOppIds = savedRepository.findByUserId(userId).stream()
            .map(s -> s.getOpportunity().getId())
            .collect(Collectors.toSet());

        Set<Long> appliedOppIds = applicationRepository.findByApplicantId(userId).stream()
            .map(a -> a.getOpportunity().getId())
            .collect(Collectors.toSet());

        Set<Long> relevantOppIds = new HashSet<>();
        relevantOppIds.addAll(savedOppIds);
        relevantOppIds.addAll(appliedOppIds);

        List<Opportunity> upcoming = opportunityRepository.findActivePublished().stream()
            .filter(opp -> relevantOppIds.isEmpty() || relevantOppIds.contains(opp.getId()))
            .filter(opp -> opp.getDeadline() != null && opp.getDeadline().isAfter(LocalDateTime.now()))
            .filter(opp -> opp.getDeadline().isBefore(LocalDateTime.now().plusDays(30)))
            .sorted(Comparator.comparing(Opportunity::getDeadline))
            .limit(10)
            .collect(Collectors.toList());

        return upcoming.stream().map(opp -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", opp.getId());
            map.put("title", opp.getTitle());
            map.put("type", opp.getType() != null ? opp.getType().name() : "OPPORTUNITY");
            map.put("location", opp.getLocation());
            map.put("deadline", opp.getDeadline());
            map.put("category", opp.getCategory());
            boolean isSaved = savedOppIds.contains(opp.getId());
            boolean isApplied = appliedOppIds.contains(opp.getId());
            map.put("isSaved", isSaved);
            map.put("isApplied", isApplied);
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRecentApplications(Long userId) {
        return applicationRepository.findByApplicantId(userId).stream()
            .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
            .limit(5)
            .map(app -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", app.getId());
                map.put("status", app.getStatus().name());
                map.put("appliedAt", app.getAppliedAt());
                map.put("opportunityId", app.getOpportunity().getId());
                map.put("opportunityTitle", app.getOpportunity().getTitle());
                map.put("organizationName", app.getOpportunity().getCreatedBy() != null
                    ? app.getOpportunity().getCreatedBy().getOrgName() : "Unknown");
                map.put("opportunityType", app.getOpportunity().getType() != null
                    ? app.getOpportunity().getType().name() : "OPPORTUNITY");
                return map;
            }).collect(Collectors.toList());
    }

    public Map<String, Long> getApplicationStats(Long userId) {
        List<Application> apps = applicationRepository.findByApplicantId(userId);
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total", (long) apps.size());
        stats.put("applied", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.APPLIED).count());
        stats.put("under_review", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.UNDER_REVIEW).count());
        stats.put("shortlisted", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED).count());
        stats.put("interview", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.INTERVIEW).count());
        stats.put("accepted", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.ACCEPTED).count());
        stats.put("rejected", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count());
        stats.put("withdrawn", apps.stream().filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count());
        return stats;
    }

    public Map<String, Object> getApplicationReadiness(Long userId) {
        List<Map<String, Object>> checks = new ArrayList<>();
        int totalChecks = 6;
        int passedChecks = 0;

        List<Skill> skills = skillRepository.findByUserId(userId);
        boolean hasSkills = !skills.isEmpty();
        checks.add(Map.of("label", "Skills", "done", hasSkills));
        if (hasSkills) passedChecks++;

        List<Education> education = educationRepository.findByUserId(userId);
        boolean hasEducation = !education.isEmpty();
        checks.add(Map.of("label", "Education", "done", hasEducation));
        if (hasEducation) passedChecks++;

        List<Experience> experience = experienceRepository.findByUserId(userId);
        boolean hasExperience = !experience.isEmpty();
        checks.add(Map.of("label", "Experience", "done", hasExperience));
        if (hasExperience) passedChecks++;

        List<UserDocument> documents = documentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        boolean hasDocuments = !documents.isEmpty();
        checks.add(Map.of("label", "Documents", "done", hasDocuments));
        if (hasDocuments) passedChecks++;

        boolean hasCv = documents.stream().anyMatch(d ->
            "cv".equalsIgnoreCase(d.getDocumentType()) || "resume".equalsIgnoreCase(d.getDocumentType()));
        checks.add(Map.of("label", "CV uploaded", "done", hasCv));
        if (hasCv) passedChecks++;

        double profileCompletion = seekerProfileService.calculateProfileCompletion(userId);
        boolean hasProfile = profileCompletion >= 50;
        checks.add(Map.of("label", "Profile complete", "done", hasProfile));
        if (hasProfile) passedChecks++;

        int percentage = (int) Math.round((double) passedChecks / totalChecks * 100);

        return Map.of(
            "percentage", percentage,
            "checks", checks
        );
    }
}
