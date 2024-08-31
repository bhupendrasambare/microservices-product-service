/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :2:24 am
 * Project:product service
 **/
package com.service.product.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {

    private String name;

    @Positive(message = "Minimum price must be positive")
    private Double minPrice;

    @Positive(message = "Maximum price must be positive")
    private Double maxPrice;

    private Long categoryId;

    private Boolean includeOutOfStock;
}
