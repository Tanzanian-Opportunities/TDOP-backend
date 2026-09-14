package tdop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tdop.service.UserDocumentService;
import tdop.service.UserService;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class UserDocumentController {

    private final UserDocumentService userDocumentService;
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

    @PostMapping
    public ResponseEntity<?> uploadDocument(@RequestBody Map<String, Object> body) {
        Long userId = getCurrentUserId();
        String name = (String) body.getOrDefault("name", "");
        String fileName = (String) body.getOrDefault("fileName", "");
        String fileType = (String) body.getOrDefault("fileType", "");
        Long fileSize = body.get("fileSize") != null ? ((Number) body.get("fileSize")).longValue() : 0L;
        String documentType = (String) body.getOrDefault("documentType", "other");
        String description = (String) body.getOrDefault("description", "");

        var doc = userDocumentService.uploadDocument(userId, name, fileName, fileType, fileSize, documentType, description);
        return ResponseEntity.ok(doc);
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
