package tdop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tdop.entity.UserDocument;
import tdop.exception.ForbiddenException;
import tdop.exception.ResourceNotFoundException;
import tdop.repository.UserDocumentRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDocumentService {

    private final UserDocumentRepository userDocumentRepository;
    private final FileStorageService fileStorageService;

    public List<UserDocument> getUserDocuments(Long userId) {
        return userDocumentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<UserDocument> getUserDocumentsByType(Long userId, String documentType) {
        return userDocumentRepository.findByUserIdAndDocumentTypeOrderByCreatedAtDesc(userId, documentType);
    }

    public UserDocument getDocument(Long userId, Long documentId) {
        UserDocument doc = userDocumentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!doc.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to document");
        }
        return doc;
    }

    public UserDocument uploadDocument(Long userId, String name, MultipartFile file, String documentType, String description) {
        String storedFilename = null;
        String fileName = "";
        String fileType = "";
        long fileSize = 0;

        if (file != null && !file.isEmpty()) {
            storedFilename = fileStorageService.storeFile(file, "documents/" + userId);
            fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
            fileType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            fileSize = file.getSize();
        }

        UserDocument doc = UserDocument.builder()
            .user(tdop.entity.User.builder().id(userId).build())
            .name(name)
            .fileName(fileName)
            .fileType(fileType)
            .fileSize(fileSize)
            .documentType(documentType)
            .description(description)
            .filePath(storedFilename)
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
        if (doc.getFilePath() != null && !doc.getFilePath().isEmpty()) {
            fileStorageService.deleteFile("documents/" + userId, doc.getFilePath());
        }
        userDocumentRepository.delete(doc);
    }

    public long getDocumentCount(Long userId) {
        return userDocumentRepository.countByUserId(userId);
    }
}
