/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :2:51 am
 * Project:product service
 **/
package com.service.product.controller;

import com.service.product.dto.request.ProductFilterRequest;
import com.service.product.dto.response.ProductResponseDTO;
import com.service.product.dto.response.Response;
import com.service.product.service.CategoryService;
import com.service.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/public")
@Tag(name = "Products Public Controller", description = "APIs for Public use")
public class PublicController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Fetch products based on filters", description = "Fetches products with optional filters: name, price range, category ID, and availability.")
    @PostMapping("/search")
    public ResponseEntity<Response> fetchProducts(@RequestBody ProductFilterRequest filterRequest) {
        return productService.fetchProducts(filterRequest);
    }

    @Operation(summary = "Fetch products based on id", description = "Fetches products with Product Id")
    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "id") Long id) {
        return productService.findById(id);
    }

    @Operation(summary = "Get all categories", description = "Fetches all categories.")
    @GetMapping("/category/get")
    public ResponseEntity<Response> getAllCategories() {
        return categoryService.getAllCategories();
    }

}
