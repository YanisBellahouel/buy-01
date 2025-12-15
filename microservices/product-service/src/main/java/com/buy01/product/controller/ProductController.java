package com.buy01.product.controller;

import com.buy01.product.dto.CreateProductRequest;
import com.buy01.product.dto.ProductResponse;
import com.buy01.product.dto.UpdateProductRequest;
import com.buy01.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> createProduct(
            Authentication authentication,
            @Valid @RequestBody CreateProductRequest request) {

        String userId = authentication.getName();
        ProductResponse product = productService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        String userId = authentication.getName();
        List<ProductResponse> products = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {

        String userId = authentication.getName();
        ProductResponse product = productService.updateProduct(userId, id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, String>> deleteProduct(
            Authentication authentication,
            @PathVariable String id) {

        String userId = authentication.getName();
        productService.deleteProduct(userId, id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }
}
