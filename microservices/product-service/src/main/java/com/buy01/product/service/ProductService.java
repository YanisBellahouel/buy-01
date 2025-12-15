package com.buy01.product.service;

import com.buy01.product.dto.CreateProductRequest;
import com.buy01.product.dto.ProductResponse;
import com.buy01.product.dto.UpdateProductRequest;
import com.buy01.product.exception.ResourceNotFoundException;
import com.buy01.product.exception.UnauthorizedException;
import com.buy01.product.model.Product;
import com.buy01.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProductResponse createProduct(String userId, CreateProductRequest request) {
        log.info("Creating product for user: {}", userId);

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUserId(userId);
        product.setImageIds(request.getImageIds());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        product = productRepository.save(product);
        log.info("Product created successfully with id: {}", product.getId());

        // Envoyer événement Kafka
        try {
            String message = String.format("PRODUCT_CREATED:%s:%s", product.getId(), userId);
            kafkaTemplate.send("product-events", message);
        } catch (Exception e) {
            log.error("Failed to send Kafka event", e);
        }

        return mapToResponse(product);
    }

    public ProductResponse getProductById(String productId) {
        log.info("Fetching product with id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return mapToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByUserId(String userId) {
        log.info("Fetching products for user: {}", userId);
        return productRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(String userId, String productId, UpdateProductRequest request) {
        log.info("Updating product {} by user {}", productId, userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Vérifier que l'utilisateur est le propriétaire
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this product");
        }

        // Mettre à jour les champs
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getImageIds() != null) {
            product.setImageIds(request.getImageIds());
        }

        product.setUpdatedAt(LocalDateTime.now());
        product = productRepository.save(product);

        log.info("Product updated successfully: {}", productId);

        // Envoyer événement Kafka
        try {
            String message = String.format("PRODUCT_UPDATED:%s:%s", productId, userId);
            kafkaTemplate.send("product-events", message);
        } catch (Exception e) {
            log.error("Failed to send Kafka event", e);
        }

        return mapToResponse(product);
    }

    public void deleteProduct(String userId, String productId) {
        log.info("Deleting product {} by user {}", productId, userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Vérifier que l'utilisateur est le propriétaire
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this product");
        }

        productRepository.delete(product);
        log.info("Product deleted successfully: {}", productId);

        // Envoyer événement Kafka
        try {
            String message = String.format("PRODUCT_DELETED:%s:%s", productId, userId);
            kafkaTemplate.send("product-events", message);
        } catch (Exception e) {
            log.error("Failed to send Kafka event", e);
        }
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setUserId(product.getUserId());
        response.setImageIds(product.getImageIds());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}