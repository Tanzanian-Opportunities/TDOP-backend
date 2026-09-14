package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.VerificationRequest;
import tdop.entity.VerificationDocument;
import tdop.entity.enums.VerificationStatus;
import tdop.exception.BadRequestException;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.VerificationRequestRepository;
import tdop.repository.VerificationDocumentRepository;
import tdop.repository.OrganizationProfileRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationOfficerService {

    private final VerificationRequestRepository verificationRequestRepository;
    private final VerificationDocumentRepository verificationDocumentRepository;
    private final OrganizationProfileRepository organizationProfileRepository;

    public VerificationRequest submitVerification(Long orgId, String document) {
        var org = organizationProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        VerificationRequest request = VerificationRequest.builder()
            .organization(org)
            .document(document)
            .status(VerificationStatus.PENDING)
            .build();
        return verificationRequestRepository.save(request);
    }

    public VerificationRequest submitVerificationWithDocs(Long orgId, List<String> documentUrls, String notes) {
        var org = organizationProfileRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        VerificationRequest request = VerificationRequest.builder()
            .organization(org)
            .document(documentUrls != null && !documentUrls.isEmpty() ? documentUrls.get(0) : "")
            .status(VerificationStatus.PENDING)
            .notes(notes)
            .build();
        VerificationRequest saved = verificationRequestRepository.save(request);

        if (documentUrls != null) {
            for (String url : documentUrls) {
                VerificationDocument doc = VerificationDocument.builder()
                    .verificationRequest(saved)
                    .documentUrl(url)
                    .build();
                verificationDocumentRepository.save(doc);
            }
        }
        return saved;
    }

    public VerificationRequest reviewRequest(Long id, VerificationStatus status, String reviewedBy, String reason) {
        VerificationRequest request = verificationRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));
        request.setStatus(status);
        request.setReviewedBy(reviewedBy);
        request.setReviewedAt(LocalDateTime.now());

        if (status == VerificationStatus.APPROVED) {
            request.getOrganization().setVerified(true);
            request.getOrganization().setVerifiedAt(LocalDateTime.now());
        } else if (status == VerificationStatus.REJECTED) {
            request.setRejectionReason(reason);
        }
        return verificationRequestRepository.save(request);
    }

    public VerificationRequest requestMoreInformation(Long id, String information) {
        VerificationRequest request = verificationRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Verification not found"));
        request.setRequestedInformation(information);
        return verificationRequestRepository.save(request);
    }

    public List<VerificationRequest> getPendingRequests() {
        return verificationRequestRepository.findByStatus(VerificationStatus.PENDING);
    }

    public List<VerificationRequest> getRequestsByOrg(Long orgId) {
        return verificationRequestRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
    }

    public VerificationRequest getRequestById(Long id) {
        return verificationRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Verification request not found"));
    }

    public List<VerificationDocument> getDocuments(Long requestId) {
        return verificationDocumentRepository.findByVerificationRequestId(requestId);
    }

    public long countPending() {
        return verificationRequestRepository.countByStatus(VerificationStatus.PENDING);
    }
}
