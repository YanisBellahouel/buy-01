package com.buy01.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private Double price;

    private Integer quantity;

    private String userId;  // ID du seller propriétaire

    private List<String> imageIds = new ArrayList<>();  // IDs des images dans Media Service

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}