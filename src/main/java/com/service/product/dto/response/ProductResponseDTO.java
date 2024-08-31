/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :2:29 am
 * Project:product service
 **/
package com.service.product.dto.response;

import com.service.product.dto.dto.ProductAttributeDto;
import com.service.product.dto.dto.ProductImageDTO;
import com.service.product.dto.dto.ProductReviewDTO;
import com.service.product.model.Product;
import java.util.List;

import com.service.product.model.ProductAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Product product;
    private List<ProductImageDTO> productImages;
    private List<ProductReviewDTO> productReviews;
    private List<ProductAttributeDto> productAttributes;
}
