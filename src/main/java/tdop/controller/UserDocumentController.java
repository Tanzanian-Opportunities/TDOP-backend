package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tdop.entity.UserDocument;
import tdop.service.FileStorageService;
import tdop.service.UserDocumentService;
import tdop.service.UserService;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class UserDocumentController {

    private final UserDocumentService userDocumentService;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> listDocuments() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(userDocumentService.getUserDocuments(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocument(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(userDocumentService.getDocument(userId, id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadDocument(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "documentType", defaultValue = "other") String documentType,
            @RequestParam(value = "description", defaultValue = "") String description) {
        Long userId = getCurrentUserId();
        var doc = userDocumentService.uploadDocument(userId, name, file, documentType, description);
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        UserDocument doc = userDocumentService.getDocument(userId, id);
        if (doc.getFilePath() == null || doc.getFilePath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        byte[] fileBytes = fileStorageService.loadFile("documents/" + userId, doc.getFilePath());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
            .contentType(MediaType.parseMediaType(doc.getFileType()))
            .contentLength(fileBytes.length)
            .body(new ByteArrayResource(fileBytes));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        return ResponseEntity.ok(userDocumentService.updateDocument(userId, id, name, description));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        userDocumentService.deleteDocument(userId, id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserIdByEmail(email);
    }
}
