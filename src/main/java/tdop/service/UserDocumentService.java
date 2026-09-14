package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tdop.entity.UserDocument;
import tdop.repository.UserDocumentRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDocumentService {

    private final UserDocumentRepository userDocumentRepository;

    public List<UserDocument> getUserDocuments(Long userId) {
        return userDocumentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<UserDocument> getUserDocumentsByType(Long userId, String documentType) {
        return userDocumentRepository.findByUserIdAndDocumentTypeOrderByCreatedAtDesc(userId, documentType);
    }

    public UserDocument getDocument(Long userId, Long documentId) {
        UserDocument doc = userDocumentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        if (!doc.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to document");
        }
        return doc;
    }

    public UserDocument uploadDocument(Long userId, String name, String fileName, String fileType, Long fileSize, String documentType, String description) {
        UserDocument doc = UserDocument.builder()
            .user(tdop.entity.User.builder().id(userId).build())
            .name(name)
            .fileName(fileName)
            .fileType(fileType)
            .fileSize(fileSize)
            .documentType(documentType)
            .description(description)
            .build();
        return userDocumentRepository.save(doc);
    }

    public UserDocument updateDocument(Long userId, Long documentId, String name, String description) {
        UserDocument doc = getDocument(userId, documentId);
        if (name != null) doc.setName(name);
        if (description != null) doc.setDescription(description);
        return userDocumentRepository.save(doc);
    }

    public void deleteDocument(Long userId, Long documentId) {
        UserDocument doc = getDocument(userId, documentId);
        userDocumentRepository.delete(doc);
    }

    public long getDocumentCount(Long userId) {
        return userDocumentRepository.countByUserId(userId);
    }
}
