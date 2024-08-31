/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :1:56 am
 * Project:product service
 **/
package com.service.product.dto.request;

import com.service.product.dto.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductRequest(
        @NotBlank(message = "Name is mandatory")
        @Size(max = 100, message = "Name must be less than 100 characters")
        String name,

        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,

        @NotBlank(message = "SKU is mandatory")
        @Size(max = 50, message = "SKU must be less than 50 characters")
        String sku,

        @NotNull(message = "Price is mandatory")
        @PositiveOrZero(message = "Price must be zero or positive")
        Double price,

        @PositiveOrZero(message = "Discount Price must be zero or positive")
        Double discountPrice,

        @NotNull(message = "Quantity is mandatory")
        @PositiveOrZero(message = "Quantity must be zero or positive")
        Integer quantity,

        @NotBlank(message = "Status is mandatory")
        @Size(max = 20, message = "Status must be less than 20 characters")
        ProductStatus status,

        @NotNull(message = "Category IDs are mandatory")
        List<Long> categoryIds
) {}
