package com.buy01.media.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponse {
    private String id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String imagePath;
    private String productId;
    private LocalDateTime createdAt;
}