package com.buy01.media.controller;

import com.buy01.media.dto.MediaResponse;
import com.buy01.media.dto.MediaUploadResponse;
import com.buy01.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class MediaController {

	private final MediaService mediaService;

	@Value("${media.upload.dir}")
	private String uploadDir;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<MediaUploadResponse> uploadMedia(
			Authentication authentication,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "productId", required = false) String productId) {

		String userId = authentication.getName();
		MediaUploadResponse response = mediaService.uploadMedia(userId, file, productId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<MediaResponse> getMediaById(@PathVariable String id) {
		MediaResponse media = mediaService.getMediaById(id);
		return ResponseEntity.ok(media);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<MediaResponse>> getMediaByProductId(@PathVariable String productId) {
		List<MediaResponse> mediaList = mediaService.getMediaByProductId(productId);
		return ResponseEntity.ok(mediaList);
	}

	@GetMapping("/my-media")
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<List<MediaResponse>> getMyMedia(Authentication authentication) {
		String userId = authentication.getName();
		List<MediaResponse> mediaList = mediaService.getMediaByUserId(userId);
		return ResponseEntity.ok(mediaList);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<Map<String, String>> deleteMedia(
			Authentication authentication,
			@PathVariable String id) {

		String userId = authentication.getName();
		mediaService.deleteMedia(userId, id);
		return ResponseEntity.ok(Map.of("message", "Media deleted successfully"));
	}

	@GetMapping("/{id}/file")
public ResponseEntity<byte[]> getMediaFile(@PathVariable String id) {
    try {
        MediaResponse media = mediaService.getMediaById(id);
        Path filePath = Paths.get(uploadDir).resolve(media.getImagePath().replace("/uploads/", ""));

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = Files.readAllBytes(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(media.getContentType()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    } catch (IOException e) {
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
}