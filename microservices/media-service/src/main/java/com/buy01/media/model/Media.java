package com.buy01.media.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media")
public class Media {

    @Id
    private String id;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String imagePath;  // Chemin ou URL de l'image stockée

    private String productId;  // ID du produit associé

    private String userId;  // ID du seller qui a uploadé

    private LocalDateTime createdAt;
}