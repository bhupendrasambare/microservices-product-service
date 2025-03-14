/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :1:11 am
 * Project:product service
 **/
package com.service.product.controller;

import com.service.product.dto.request.CategoryRequest;
import com.service.product.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import com.service.product.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/categories")
@Tag(name = "Categories Controller", description = "APIs for Product Categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Create a new category", description = "Creates a new category and returns the created category.")
    @PostMapping
    public ResponseEntity<Response> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }

    @Operation(summary = "Update a category", description = "Updates an existing category by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.updateCategory(id,categoryRequest);
    }

}
