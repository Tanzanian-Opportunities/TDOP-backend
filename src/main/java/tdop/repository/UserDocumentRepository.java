package tdop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tdop.entity.UserDocument;
import java.util.List;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {
    List<UserDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserDocument> findByUserIdAndDocumentTypeOrderByCreatedAtDesc(Long userId, String documentType);
    long countByUserId(Long userId);
}
