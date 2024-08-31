/**
 * author @bhupendrasambare
 * Date   :01/09/24
 * Time   :1:41 am
 * Project:product service
 **/
package com.service.product.controller;


import com.service.product.dto.request.ProductAttributeRequest;
import com.service.product.dto.response.Response;
import com.service.product.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/attributes")
@Tag(name = "Product Attributes Controller", description = "APIs for managing product attributes")
public class ProductAttributesController {

    @Autowired
    private ProductAttributeService productAttributeService;

    @Operation(summary = "Create a new product attribute", description = "Creates a new product attribute and returns the created attribute.")
    @PostMapping
    public ResponseEntity<Response> createProductAttribute(@Valid @RequestBody ProductAttributeRequest productAttributeRequest) {
        return productAttributeService.createProductAttribute(productAttributeRequest);
    }

    @Operation(summary = "Get all product attributes", description = "Fetches all product attributes.")
    @GetMapping
    public ResponseEntity<Response> getAllProductAttributes() {
        return productAttributeService.getAllProductAttribute();
    }

    @Operation(summary = "Update a product attribute", description = "Updates an existing product attribute by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateProductAttribute(@PathVariable Long id, @Valid @RequestBody ProductAttributeRequest productAttributeRequest) {
        return productAttributeService.updateProductAttribute(id, productAttributeRequest);
    }

    @Operation(summary = "Delete a product attribute", description = "Deletes an existing product attribute by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProductAttribute(@PathVariable Long id) {
        return productAttributeService.deleteProductAttribute(id);
    }
}
