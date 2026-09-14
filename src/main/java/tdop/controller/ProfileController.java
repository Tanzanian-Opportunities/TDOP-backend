package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tdop.dto.request.SkillRequest;
import tdop.entity.CareerGoal;
import tdop.entity.Experience;
import tdop.service.SeekerProfileService;
import tdop.service.UserService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SeekerProfileService seekerProfileService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(seekerProfileService.getSeekerProfile(userId));
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(seekerProfileService.updateProfile(userId, request));
    }

    @GetMapping("/completion")
    public ResponseEntity<?> getProfileCompletion() {
        Long userId = getCurrentUserId();
        double completion = seekerProfileService.calculateProfileCompletion(userId);
        return ResponseEntity.ok(Map.of("completion", completion));
    }

    @PutMapping("/visibility")
    public ResponseEntity<?> updateVisibility(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        String visibility = body.getOrDefault("visibility", "PUBLIC");
        seekerProfileService.updateProfileVisibility(userId, visibility);
        return ResponseEntity.ok(Map.of("visibility", visibility));
    }

    @PutMapping("/notification-preference")
    public ResponseEntity<?> updateNotificationPreference(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        String preference = body.getOrDefault("preference", "ALL");
        seekerProfileService.updateNotificationPreference(userId, preference);
        return ResponseEntity.ok(Map.of("notificationPreference", preference));
    }

    @PostMapping("/skills")
    public ResponseEntity<?> addSkill(@RequestBody SkillRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(seekerProfileService.addSkill(userId, request));
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<?> removeSkill(@PathVariable Long skillId) {
        Long userId = getCurrentUserId();
        seekerProfileService.removeSkill(userId, skillId);
        return ResponseEntity.ok("Skill removed");
    }

    @PostMapping("/education")
    public ResponseEntity<?> addEducation(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(seekerProfileService.addEducation(userId, request));
    }

    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<?> removeEducation(@PathVariable Long educationId) {
        Long userId = getCurrentUserId();
        seekerProfileService.removeEducation(userId, educationId);
        return ResponseEntity.ok("Education removed");
    }

    @PostMapping("/interests")
    public ResponseEntity<?> addInterest(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(seekerProfileService.addInterest(userId, request));
    }

    @DeleteMapping("/interests/{interestId}")
    public ResponseEntity<?> removeInterest(@PathVariable Long interestId) {
        Long userId = getCurrentUserId();
        seekerProfileService.removeInterest(userId, interestId);
        return ResponseEntity.ok("Interest removed");
    }

    @PostMapping("/experience")
    public ResponseEntity<?> addExperience(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        Experience exp = seekerProfileService.addExperience(
            userId,
            (String) request.get("company"),
            (String) request.get("title"),
            (String) request.get("location"),
            request.get("startDate") != null ? LocalDate.parse((String) request.get("startDate")) : null,
            request.get("endDate") != null ? LocalDate.parse((String) request.get("endDate")) : null,
            Boolean.TRUE.equals(request.get("isCurrent")),
            (String) request.get("description")
        );
        return ResponseEntity.ok(exp);
    }

    @DeleteMapping("/experience/{experienceId}")
    public ResponseEntity<?> removeExperience(@PathVariable Long experienceId) {
        Long userId = getCurrentUserId();
        seekerProfileService.removeExperience(userId, experienceId);
        return ResponseEntity.ok("Experience removed");
    }

    @PostMapping("/career-goals")
    public ResponseEntity<?> addCareerGoal(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        CareerGoal goal = seekerProfileService.addCareerGoal(
            userId,
            (String) request.get("title"),
            (String) request.get("description"),
            (String) request.get("targetIndustry"),
            (String) request.get("targetRole"),
            (String) request.get("timeline")
        );
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/career-goals/{goalId}")
    public ResponseEntity<?> removeCareerGoal(@PathVariable Long goalId) {
        Long userId = getCurrentUserId();
        seekerProfileService.removeCareerGoal(userId, goalId);
        return ResponseEntity.ok("Career goal removed");
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserIdByEmail(email);
    }
}
