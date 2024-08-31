/**
 * author @bhupendrasambare
 * Date   :01/09/24
 * Time   :1:42 am
 * Project:product service
 **/
package com.service.product.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProductAttributeRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        String name,

        String value
) {
}
