package com.buy01.media.service;

import com.buy01.media.dto.MediaResponse;
import com.buy01.media.dto.MediaUploadResponse;
import com.buy01.media.exception.BadRequestException;
import com.buy01.media.exception.ResourceNotFoundException;
import com.buy01.media.exception.UnauthorizedException;
import com.buy01.media.model.Media;
import com.buy01.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final MediaRepository mediaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${media.upload.dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    public MediaUploadResponse uploadMedia(String userId, MultipartFile file, String productId) {
        log.info("Uploading media for user: {} and product: {}", userId, productId);

        // Validation de la taille du fichier
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 2MB");
        }

        // Validation du type de fichier
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Invalid file type. Only images are allowed (JPEG, PNG, GIF, WebP)");
        }

        // Validation que le fichier n'est pas vide
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            // Créer le dossier d'upload s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Créer l'entité Media
            Media media = new Media();
            media.setFileName(originalFilename);
            media.setContentType(contentType);
            media.setFileSize(file.getSize());
            media.setImagePath("/uploads/" + uniqueFileName);
            media.setProductId(productId);
            media.setUserId(userId);
            media.setCreatedAt(LocalDateTime.now());

            media = mediaRepository.save(media);
            log.info("Media uploaded successfully with id: {}", media.getId());

            // Envoyer événement Kafka
            try {
                String message = String.format("MEDIA_UPLOADED:%s:%s:%s",
                    media.getId(), userId, productId);
                kafkaTemplate.send("media-events", message);
            } catch (Exception e) {
                log.error("Failed to send Kafka event", e);
            }

            return mapToUploadResponse(media);

        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public MediaResponse getMediaById(String mediaId) {
        log.info("Fetching media with id: {}", mediaId);
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + mediaId));

        return mapToResponse(media);
    }

    public List<MediaResponse> getMediaByProductId(String productId) {
        log.info("Fetching media for product: {}", productId);
        return mediaRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MediaResponse> getMediaByUserId(String userId) {
        log.info("Fetching media for user: {}", userId);
        return mediaRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteMedia(String userId, String mediaId) {
        log.info("Deleting media {} by user {}", mediaId, userId);

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + mediaId));

        // Vérifier que l'utilisateur est le propriétaire
        if (!media.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this media");
        }

        try {
            // Supprimer le fichier physique
            Path filePath = Paths.get(uploadDir).resolve(media.getImagePath().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);

            // Supprimer de la base de données
            mediaRepository.delete(media);
            log.info("Media deleted successfully: {}", mediaId);

            // Envoyer événement Kafka
            try {
                String message = String.format("MEDIA_DELETED:%s:%s", mediaId, userId);
                kafkaTemplate.send("media-events", message);
            } catch (Exception e) {
                log.error("Failed to send Kafka event", e);
            }

        } catch (IOException e) {
            log.error("Error deleting file", e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    private MediaUploadResponse mapToUploadResponse(Media media) {
        return new MediaUploadResponse(
            media.getId(),
            media.getFileName(),
            media.getContentType(),
            media.getFileSize(),
            media.getImagePath(),
            media.getProductId(),
            media.getCreatedAt()
        );
    }

    private MediaResponse mapToResponse(Media media) {
        MediaResponse response = new MediaResponse();
        response.setId(media.getId());
        response.setFileName(media.getFileName());
        response.setContentType(media.getContentType());
        response.setFileSize(media.getFileSize());
        response.setImagePath(media.getImagePath());
        response.setProductId(media.getProductId());
        response.setUserId(media.getUserId());
        response.setCreatedAt(media.getCreatedAt());
        return response;
    }
}