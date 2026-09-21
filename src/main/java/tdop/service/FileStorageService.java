package tdop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tdop.config.FileStorageConfig;
import tdop.exception.BadRequestException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "application/pdf",
        "image/jpeg", "image/png",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain"
    );

    public String storeFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType);
        }
        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new BadRequestException("File exceeds maximum size of " + (fileStorageConfig.getMaxFileSize() / 1024 / 1024) + "MB");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BadRequestException("Invalid filename");
        }
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path uploadDir = Paths.get(fileStorageConfig.getUploadDir(), subDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path targetLocation = uploadDir.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored: {}/{}", subDir, storedFilename);
            return storedFilename;
        } catch (IOException e) {
            throw new BadRequestException("Could not store file: " + e.getMessage());
        }
    }

    public byte[] loadFile(String subDir, String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new BadRequestException("Invalid filename");
        }
        try {
            Path filePath = Paths.get(fileStorageConfig.getUploadDir(), subDir, filename).toAbsolutePath().normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new BadRequestException("File not found: " + filename);
        }
    }

    public void deleteFile(String subDir, String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new BadRequestException("Invalid filename");
        }
        try {
            Path filePath = Paths.get(fileStorageConfig.getUploadDir(), subDir, filename).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}/{}", subDir, filename);
        } catch (IOException e) {
            log.warn("Could not delete file: {}", filename);
        }
    }
}
