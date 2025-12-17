package com.buy01.media.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MediaResponse {
    private String id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String imagePath;
    private String productId;
    private String userId;
    private LocalDateTime createdAt;
}