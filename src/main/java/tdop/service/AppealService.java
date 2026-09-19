package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Appeal;
import tdop.entity.User;
import tdop.entity.enums.AppealStatus;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.AppealRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AppealService {

    private final AppealRepository appealRepository;
    private final UserRepository userRepository;

    public Appeal createAppeal(String targetType, Long targetId, String reason, String description, Long appellantId) {
        User appellant = userRepository.findById(appellantId).orElse(null);
        Appeal appeal = Appeal.builder()
            .targetType(targetType)
            .targetId(targetId)
            .reason(reason)
            .description(description)
            .status(AppealStatus.PENDING)
            .appellant(appellant)
            .build();
        return appealRepository.save(appeal);
    }

    public List<Appeal> getPendingAppeals() {
        return appealRepository.findByStatusOrderByCreatedAtDesc(AppealStatus.PENDING);
    }

    public List<Appeal> getAllAppeals() {
        return appealRepository.findAll();
    }

    public long countPending() {
        return appealRepository.countByStatus(AppealStatus.PENDING);
    }

    public Appeal getAppealById(Long id) {
        return appealRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appeal not found"));
    }

    public Appeal reviewAppeal(Long id, Long reviewerId, String status, String notes) {
        Appeal appeal = getAppealById(id);
        User reviewer = userRepository.findById(reviewerId)
            .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        appeal.setReviewedBy(reviewer);
        appeal.setStatus(AppealStatus.valueOf(status));
        appeal.setReviewNotes(notes);
        appeal.setReviewedAt(LocalDateTime.now());
        return appealRepository.save(appeal);
    }

    public Appeal upholdAppeal(Long id, String resolution) {
        Appeal appeal = getAppealById(id);
        appeal.setStatus(AppealStatus.UPHELD);
        appeal.setResolution(resolution);
        appeal.setReviewedAt(LocalDateTime.now());
        return appealRepository.save(appeal);
    }

    public Appeal overruleAppeal(Long id, String resolution) {
        Appeal appeal = getAppealById(id);
        appeal.setStatus(AppealStatus.OVERRULED);
        appeal.setResolution(resolution);
        appeal.setReviewedAt(LocalDateTime.now());
        return appealRepository.save(appeal);
    }

    public Appeal dismissAppeal(Long id, String reason) {
        Appeal appeal = getAppealById(id);
        appeal.setStatus(AppealStatus.DISMISSED);
        appeal.setResolution(reason);
        appeal.setReviewedAt(LocalDateTime.now());
        return appealRepository.save(appeal);
    }
}
