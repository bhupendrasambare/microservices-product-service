/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :2:08 am
 * Project:product service
 **/
package com.service.product.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateImageDto {
    private MultipartFile file;
    private Boolean primary;
}
