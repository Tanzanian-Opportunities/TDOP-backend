package tdop.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.VerificationRequest;
import tdop.entity.enums.VerificationStatus;
import tdop.entity.OrganizationProfile;
import tdop.repository.VerificationRequestRepository;
import tdop.repository.OrganizationProfileRepository;
import tdop.exception.ResourceNotFoundException;

@Service
@Transactional
public class VerificationService {

    private final VerificationRequestRepository verificationRequestRepository;
    private final OrganizationProfileRepository organizationProfileRepository;

    public VerificationService(VerificationRequestRepository verificationRequestRepository,
                                OrganizationProfileRepository organizationProfileRepository) {
        this.verificationRequestRepository = verificationRequestRepository;
        this.organizationProfileRepository = organizationProfileRepository;
    }

    public VerificationRequest submitVerification(Long orgId, String document) {
        OrganizationProfile org = organizationProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        VerificationRequest request = VerificationRequest.builder()
            .organization(org).document(document).status(VerificationStatus.PENDING)
            .build();
        return verificationRequestRepository.save(request);
    }

    public VerificationRequest reviewRequest(Long id, VerificationStatus status, String reviewedBy) {
        VerificationRequest request = verificationRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));
        request.setStatus(status);
        request.setReviewedBy(reviewedBy);
        request.setReviewedAt(java.time.LocalDateTime.now());
        if (status == VerificationStatus.APPROVED) {
            request.getOrganization().setVerified(true);
            request.getOrganization().setVerifiedAt(java.time.LocalDateTime.now());
        }
        return verificationRequestRepository.save(request);
    }

    public java.util.List<VerificationRequest> getPendingRequests() {
        return verificationRequestRepository.findAll().stream()
            .filter(r -> r.getStatus() == VerificationStatus.PENDING)
            .toList();
    }
}
