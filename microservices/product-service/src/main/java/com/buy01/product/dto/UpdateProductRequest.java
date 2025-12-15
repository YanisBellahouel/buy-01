package com.buy01.product.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductRequest {

    private String name;

    private String description;

    @Min(value = 0, message = "Price must be positive")
    private Double price;

    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantity;

    private List<String> imageIds;
}