/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :1:54 am
 * Project:product service
 **/
package com.service.product.controller;

import com.service.product.dto.enums.ProductStatus;
import com.service.product.dto.request.ProductFilterRequest;
import com.service.product.dto.request.ProductRequest;
import com.service.product.dto.request.ProductUpdateImageDto;
import com.service.product.dto.request.UpdateProductImage;
import com.service.product.dto.response.Response;
import com.service.product.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import com.service.product.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name = "Products Controller", description = "APIs for Product Management")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Fetch products based on login user", description = "Fetches products with Product Id")
    @GetMapping("/get")
    public ResponseEntity<?> getProduct() {
        return productService.findAll();
    }

    @Operation(summary = "Create a new product", description = "Creates a new product with associated categories.")
    @PostMapping("/add")
    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @Operation(summary = "Upload images to a product", description = "Uploads a list of images to the specified product.")
    @PostMapping("/{productId}/images")
    public ResponseEntity<Response> uploadProductImages(@PathVariable Long productId, @RequestBody ProductUpdateImageDto imageUrls) {
        return productService.uploadProductImages(productId, imageUrls);
    }

    @Operation(summary = "Update product discount", description = "Updates the discount of a product.")
    @PutMapping("/{productId}/discount")
    public ResponseEntity<Response> updateProductDiscount(@PathVariable Long productId, @RequestParam Double discountPrice) {
        return productService.updateProductDiscount(productId, discountPrice);
    }

    @Operation(summary = "Add update product category", description = "Add update product category")
    @PutMapping("/{productId}/category/{categoryId}")
    public ResponseEntity<Response> updateProductDiscount(@PathVariable Long productId, @RequestParam Long categoryId) {
        return productService.addProductCategory(productId, categoryId);
    }

    @Operation(summary = "Delete product category", description = "Delete product category")
    @DeleteMapping("/{productId}/category/{categoryId}")
    public ResponseEntity<Response> deleteProductCategory(@PathVariable Long productId, @RequestParam Long categoryId) {
        return productService.deleteProductCategory(productId, categoryId);
    }

    @Operation(summary = "Update product quantity", description = "Updates the quantity of a product.")
    @PutMapping("/{productId}/quantity")
    public ResponseEntity<Response> updateProductQuantity(@PathVariable Long productId, @RequestParam Integer quantity) {
        return productService.updateProductQuantity(productId, quantity);
    }

    @Operation(summary = "Change product status", description = "Updates the status of a product.")
    @PutMapping("/{productId}/status")
    public ResponseEntity<Response> changeProductStatus(@PathVariable Long productId, @RequestParam ProductStatus status) {
        return productService.changeProductStatus(productId, status);
    }
}
