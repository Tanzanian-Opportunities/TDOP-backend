package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.Escalation;
import tdop.entity.User;
import tdop.entity.enums.EscalationStatus;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.EscalationRepository;
import tdop.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EscalationService {

    private final EscalationRepository escalationRepository;
    private final UserRepository userRepository;

    public Escalation createEscalation(String targetType, Long targetId, String reason, String description, Long escalatedById) {
        User escalatedBy = userRepository.findById(escalatedById).orElse(null);
        Escalation escalation = Escalation.builder()
            .targetType(targetType)
            .targetId(targetId)
            .reason(reason)
            .description(description)
            .status(EscalationStatus.OPEN)
            .escalatedBy(escalatedBy)
            .build();
        return escalationRepository.save(escalation);
    }

    public List<Escalation> getOpenEscalations() {
        return escalationRepository.findByStatusOrderByCreatedAtDesc(EscalationStatus.OPEN);
    }

    public List<Escalation> getAllEscalations() {
        return escalationRepository.findAll();
    }

    public long countOpen() {
        return escalationRepository.countByStatus(EscalationStatus.OPEN);
    }

    public Escalation getEscalationById(Long id) {
        return escalationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Escalation not found"));
    }

    public Escalation assignEscalation(Long id, Long officerId) {
        Escalation escalation = getEscalationById(id);
        User officer = userRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));
        escalation.setAssignedTo(officer);
        escalation.setStatus(EscalationStatus.IN_PROGRESS);
        return escalationRepository.save(escalation);
    }

    public Escalation resolveEscalation(Long id, String resolution) {
        Escalation escalation = getEscalationById(id);
        escalation.setStatus(EscalationStatus.RESOLVED);
        escalation.setResolution(resolution);
        escalation.setResolvedAt(LocalDateTime.now());
        return escalationRepository.save(escalation);
    }

    public Escalation dismissEscalation(Long id, String reason) {
        Escalation escalation = getEscalationById(id);
        escalation.setStatus(EscalationStatus.DISMISSED);
        escalation.setResolution(reason);
        escalation.setResolvedAt(LocalDateTime.now());
        return escalationRepository.save(escalation);
    }
}
