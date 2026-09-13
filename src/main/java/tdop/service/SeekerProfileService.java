package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.dto.request.EducationRequest;
import tdop.dto.request.InterestRequest;
import tdop.dto.request.ProfileUpdateRequest;
import tdop.dto.request.SkillRequest;
import tdop.entity.*;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SeekerProfileService {

    private final SeekerProfileRepository seekerProfileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final InterestRepository interestRepository;
    private final ExperienceRepository experienceRepository;
    private final CareerGoalRepository careerGoalRepository;

    public Map<String, Object> getSeekerProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultProfile(user));

        Map<String, Object> result = new HashMap<>();
        result.put("id", profile.getId());
        result.put("userId", userId);
        result.put("headline", profile.getBio());
        result.put("summary", profile.getBio());
        result.put("skills", profile.getSkills());
        result.put("education", profile.getEducation());
        result.put("interests", profile.getInterests());
        result.put("experiences", profile.getExperiences());
        result.put("careerGoals", profile.getCareerGoals());
        result.put("avatar", profile.getProfilePicture());
        result.put("fullName", user.getFullName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("location", profile.getLocation());
        result.put("createdAt", profile.getCreatedAt());
        return result;
    }

    public Map<String, Object> updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultProfile(user));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getProfilePicture() != null) profile.setProfilePicture(request.getProfilePicture());
        seekerProfileRepository.save(profile);

        log.info("Profile updated for user={}", userId);
        return getSeekerProfile(userId);
    }

    public Skill addSkill(Long userId, SkillRequest request) {
        SeekerProfile profile = getOrCreateProfile(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Skill skill = Skill.builder()
            .name(request.getName())
            .category(request.getCategory())
            .level(request.getLevel())
            .user(user)
            .seekerProfile(profile)
            .build();
        log.info("Skill added for user={}: {}", userId, request.getName());
        return skillRepository.save(skill);
    }

    public void removeSkill(Long userId, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        if (!skill.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only modify your own skills");
        }
        skillRepository.deleteById(skillId);
    }

    public Education addEducation(Long userId, EducationRequest request) {
        SeekerProfile profile = getOrCreateProfile(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Education education = Education.builder()
            .institution(request.getInstitution())
            .degree(request.getDegree())
            .fieldOfStudy(request.getFieldOfStudy())
            .user(user)
            .seekerProfile(profile)
            .build();
        log.info("Education added for user={}: {}", userId, request.getInstitution());
        return educationRepository.save(education);
    }

    public void removeEducation(Long userId, Long educationId) {
        Education education = educationRepository.findById(educationId)
            .orElseThrow(() -> new ResourceNotFoundException("Education not found"));
        if (!education.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only modify your own education");
        }
        educationRepository.deleteById(educationId);
    }

    public Interest addInterest(Long userId, InterestRequest request) {
        SeekerProfile profile = getOrCreateProfile(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Interest interest = Interest.builder()
            .category(request.getCategory())
            .description(request.getDescription())
            .user(user)
            .seekerProfile(profile)
            .build();
        log.info("Interest added for user={}: {}", userId, request.getCategory());
        return interestRepository.save(interest);
    }

    public void removeInterest(Long userId, Long interestId) {
        Interest interest = interestRepository.findById(interestId)
            .orElseThrow(() -> new ResourceNotFoundException("Interest not found"));
        if (!interest.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only modify your own interests");
        }
        interestRepository.deleteById(interestId);
    }

    public Experience addExperience(Long userId, String company, String title,
                                     String location, LocalDate startDate,
                                     LocalDate endDate, boolean isCurrent, String description) {
        SeekerProfile profile = getOrCreateProfile(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Experience exp = Experience.builder()
            .company(company)
            .title(title)
            .location(location)
            .startDate(startDate)
            .endDate(isCurrent ? null : endDate)
            .isCurrent(isCurrent)
            .description(description)
            .user(user)
            .seekerProfile(profile)
            .build();
        log.info("Experience added for user={}: {} at {}", userId, title, company);
        return experienceRepository.save(exp);
    }

    public void removeExperience(Long userId, Long experienceId) {
        Experience exp = experienceRepository.findById(experienceId)
            .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        if (!exp.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only modify your own experience");
        }
        experienceRepository.deleteById(experienceId);
    }

    public CareerGoal addCareerGoal(Long userId, String title, String description,
                                      String targetIndustry, String targetRole, String timeline) {
        SeekerProfile profile = getOrCreateProfile(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CareerGoal goal = CareerGoal.builder()
            .title(title)
            .description(description)
            .targetIndustry(targetIndustry)
            .targetRole(targetRole)
            .timeline(timeline)
            .user(user)
            .seekerProfile(profile)
            .build();
        log.info("Career goal added for user={}: {}", userId, title);
        return careerGoalRepository.save(goal);
    }

    public void removeCareerGoal(Long userId, Long goalId) {
        CareerGoal goal = careerGoalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Career goal not found"));
        if (!goal.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only modify your own career goals");
        }
        careerGoalRepository.deleteById(goalId);
    }

    public double calculateProfileCompletion(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
            .orElse(null);

        int totalFields = 6;
        int completedFields = 0;

        if (user.getFullName() != null && !user.getFullName().isEmpty()) completedFields++;
        if (user.getPhone() != null && !user.getPhone().isEmpty()) completedFields++;
        if (profile != null) {
            if (profile.getBio() != null && !profile.getBio().isEmpty()) completedFields++;
            if (profile.getLocation() != null && !profile.getLocation().isEmpty()) completedFields++;
            if (profile.getSkills() != null && !profile.getSkills().isEmpty()) completedFields++;
            if (profile.getEducation() != null && !profile.getEducation().isEmpty()) completedFields++;
        }

        return Math.round((double) completedFields / totalFields * 100);
    }

    private SeekerProfile getOrCreateProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return seekerProfileRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultProfile(user));
    }

    private SeekerProfile createDefaultProfile(User user) {
        SeekerProfile profile = SeekerProfile.builder()
            .user(user)
            .bio("")
            .location("")
            .build();
        return seekerProfileRepository.save(profile);
    }
}
