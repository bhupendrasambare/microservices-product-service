/**
 * author @bhupendrasambare
 * Date   :01/09/24
 * Time   :1:41 am
 * Project:product service
 **/
package com.service.product.service;

import com.service.product.config.Constants;
import com.service.product.config.Utility;
import com.service.product.dto.Status;
import com.service.product.dto.request.ProductAttributeRequest;
import com.service.product.dto.response.Response;
import com.service.product.model.Product;
import com.service.product.model.ProductAttribute;
import com.service.product.model.ProductAttribute;
import com.service.product.model.Users;
import com.service.product.repository.ProductAttributeRepository;
import com.service.product.repository.ProductRepository;
import com.service.product.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductAttributeService {

    @Autowired
    private ProductAttributeRepository productAttributesRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UsersRepository usersRepository;

    public ResponseEntity<Response> createProductAttribute(ProductAttributeRequest productAttributeRequest) {
        Product optionalProduct = productRepository.findById(productAttributeRequest.productId()).orElse(null);
        if (optionalProduct==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_NOT_FOUND_CODE, Constants.PRODUCT_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null && optionalProduct.getCreatedBy()!=null && optionalProduct.getCreatedBy().getId() != users.getId()){
            ProductAttribute productAttributes = new ProductAttribute();
            productAttributes.setProductId(optionalProduct.getId());
            productAttributes.setAttributeName(productAttributeRequest.name());
            productAttributes.setAttributeValue(productAttributeRequest.value());
            productAttributes.setCreatedBy(users.getId());

            productAttributes = productAttributesRepository.save(productAttributes);

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(Constants.PRODUCT_ATTRIBUTE_CREATED_SUCCESSFULLY, productAttributes));
        }else{
            return new ResponseEntity<>
                    (new Response(Status.FAILED,
                            Constants.UNAUTHORIZED_REQUEST_CODE,
                            Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Response> getAllProductAttribute() {
        return ResponseEntity.ok(new Response(Constants.PRODUCT_ATTRIBUTES_FETCHED_SUCCESSFULLY, productAttributesRepository.findAll()));
    }

    public ResponseEntity<Response> updateProductAttribute(Long id, ProductAttributeRequest productAttributeRequest) {
        Optional<ProductAttribute> existingAttributeOptional = productAttributesRepository.findById(id);
        if (existingAttributeOptional.isEmpty()) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_ATTRIBUTE_NOT_FOUND_CODE, Constants.PRODUCT_ATTRIBUTE_NOT_FOUND), HttpStatus.NOT_FOUND);
        }

        ProductAttribute existingAttribute = existingAttributeOptional.get();
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null && existingAttribute.getCreatedBy()!=null && existingAttribute.getCreatedBy() != users.getId()){
            if (productAttributeRequest.name() != null && !productAttributeRequest.name().isEmpty()) {
                existingAttribute.setAttributeName(productAttributeRequest.name());
            }

            if (productAttributeRequest.value() != null && !productAttributeRequest.value().isEmpty()) {
                existingAttribute.setAttributeValue(productAttributeRequest.value());
            }

            existingAttribute = productAttributesRepository.save(existingAttribute);
            return ResponseEntity.ok(new Response(Constants.PRODUCT_ATTRIBUTE_UPDATED_SUCCESSFULLY, existingAttribute));
        }else{
            return new ResponseEntity<>
                    (new Response(Status.FAILED,
                            Constants.UNAUTHORIZED_REQUEST_CODE,
                            Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Response> deleteProductAttribute(Long id) {
        ProductAttribute existingAttributeOptional = productAttributesRepository.findById(id).orElse(null);
        if (existingAttributeOptional==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_ATTRIBUTE_NOT_FOUND_CODE, Constants.PRODUCT_ATTRIBUTE_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null && existingAttributeOptional.getCreatedBy()!=null && existingAttributeOptional.getCreatedBy() != users.getId()){

            productAttributesRepository.delete(existingAttributeOptional);
            return ResponseEntity.ok(new Response(Constants.PRODUCT_ATTRIBUTE_DELETED_SUCCESSFULLY));
        }else{
            return new ResponseEntity<>
                    (new Response(Status.FAILED,
                            Constants.UNAUTHORIZED_REQUEST_CODE,
                            Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }
    }
}