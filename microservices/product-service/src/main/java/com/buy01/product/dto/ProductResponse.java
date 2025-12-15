package com.buy01.product.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String userId;
    private List<String> imageIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
