package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tdop.entity.VerificationDocument;
import java.util.List;

public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {
    List<VerificationDocument> findByVerificationRequestId(Long verificationRequestId);
}
