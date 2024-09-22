/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :1:55 am
 * Project:product service
 **/
package com.service.product.service;

import com.service.product.config.Constants;
import com.service.product.config.Utility;
import com.service.product.controller.FileController;
import com.service.product.dto.Status;
import com.service.product.dto.dto.ProductAttributeDto;
import com.service.product.dto.dto.ProductCategoryDto;
import com.service.product.dto.dto.ProductImageDTO;
import com.service.product.dto.dto.ProductReviewDTO;
import com.service.product.dto.enums.ProductStatus;
import com.service.product.dto.request.ProductFilterRequest;
import com.service.product.dto.request.ProductRequest;
import com.service.product.dto.request.ProductUpdateImageDto;
import com.service.product.dto.response.ProductResponseDTO;
import com.service.product.dto.response.Response;
import com.service.product.model.*;
import com.service.product.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileController fileController;

    public ResponseEntity<Response> createProduct(ProductRequest productRequest) {
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null){
            Product product = new Product();
            product.setName(productRequest.name());
            product.setDescription(productRequest.description());
            product.setSku(productRequest.sku());
            product.setPrice(productRequest.price());
            product.setDiscountPrice(productRequest.discountPrice());
            product.setQuantity(productRequest.quantity());
            product.setStatus(productRequest.status());
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            product.setCreatedBy(users);
            product = productRepository.save(product);

            List<Category> categories = categoryRepository.findAllById(productRequest.categoryIds());
            for(Category category:categories){
                try{
                    ProductCategory productCategory = new ProductCategory(null,product,category);
                    productCategoryRepository.save(productCategory);
                }catch (Exception e){
                    e.printStackTrace();
                    log.error(" while saving product categories");
                }
            }

            return ResponseEntity.status(201).body(new Response(Constants.PRODUCT_CREATED_SUCCESSFULLY, product));
        }else{
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.UNAUTHORIZED_REQUEST_CODE, Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public ResponseEntity<Response> uploadProductImages(Long productId, ProductUpdateImageDto images) {
        Product optionalProduct = productRepository.findById(productId).orElse(null);
        if (optionalProduct==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_NOT_FOUND_CODE, Constants.PRODUCT_NOT_FOUND), HttpStatus.NOT_FOUND);
        }else{
            try{
                Utility utility = new Utility();
                Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
                if(users!=null && optionalProduct.getCreatedBy()!=null && optionalProduct.getCreatedBy().getId() != users.getId()){
                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(optionalProduct);
                    productImage.setCreatedAt(LocalDateTime.now());
                    productImage.setImageUrl(saveImage(images.getFile()));
                    productImage.setIsPrimary(images.getPrimary());
                    productImage = productImageRepository.save(productImage);
                    return ResponseEntity.ok(new Response(Constants.IMAGES_UPLOADED_SUCCESSFULLY,productImage));
                }else{
                    return new ResponseEntity<>
                            (new Response(Status.FAILED,
                                    Constants.UNAUTHORIZED_REQUEST_CODE,
                                    Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
                }
            }catch ( Exception e){
                e.printStackTrace();
                log.error(" in updating product images ERROR:{}",e.getMessage());
                return ResponseEntity.ok(new Response(Status.FAILED,Constants.ERROR_UPLOADING_IMAGE_CODE,Constants.ERROR_UPLOADING_IMAGE,e.getMessage()));
            }
        }
    }

    @Transactional
    public ResponseEntity<Response> addProductCategory(Long productId, Long categoryId) {
        Product optionalProduct = productRepository.findById(productId).orElse(null);
        if (optionalProduct==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_NOT_FOUND_CODE, Constants.PRODUCT_NOT_FOUND), HttpStatus.NOT_FOUND);
        }else{
            Utility utility = new Utility();
            Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
            if(users!=null && optionalProduct.getCreatedBy()!=null && optionalProduct.getCreatedBy().getId() != users.getId()){
                Category category = categoryRepository.findById(categoryId).orElse(null);
                if(category!=null){
                    ProductCategory productCategory = new ProductCategory(null,optionalProduct,category);
                    if(productCategoryRepository.existByProductAndCategory(productId, categoryId)){
                        productCategoryRepository.save(productCategory);
                    }
                    return new ResponseEntity<>(new Response(Constants.PRODUCT_CATEGORY_ADDED), HttpStatus.NOT_FOUND);
                }else{
                    return new ResponseEntity<>(new Response(Status.FAILED, Constants.CATEGORY_NOT_FOUND_CODE, Constants.CATEGORY_NOT_FOUND), HttpStatus.NOT_FOUND);
                }
            }else{
                return new ResponseEntity<>
                        (new Response(Status.FAILED,
                                Constants.UNAUTHORIZED_REQUEST_CODE,
                                Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @Transactional
    public ResponseEntity<Response> deleteProductCategory(Long productId, Long categoryId) {
        if(productCategoryRepository.existByProductAndCategory(productId, categoryId)){
            List<ProductCategory> productCategoryList = productCategoryRepository.findByProductAndCategory(productId,categoryId);
            productCategoryRepository.deleteAll(productCategoryList);
            return new ResponseEntity<>(new Response(Constants.PRODUCT_CATEGORY_DELETED), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_CATEGORY_NOT_FOUND_CODE, Constants.PRODUCT_CATEGORY_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    private String saveImage(MultipartFile multipartFile) {
        return fileController.uploadFile(multipartFile).getBody();
    }

    @Transactional
    public ResponseEntity<Response> updateProductDiscount(Long productId, Double discountPrice) {
        Product optionalProduct = productRepository.findById(productId).orElse(null);
        if (optionalProduct==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_NOT_FOUND_CODE, Constants.PRODUCT_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null && optionalProduct.getCreatedBy()!=null && optionalProduct.getCreatedBy().getId() != users.getId()){

            Product product = optionalProduct;
            product.setDiscountPrice(discountPrice);
            product.setUpdatedAt(LocalDateTime.now());
            optionalProduct = productRepository.save(product);

            return ResponseEntity.ok(new Response(Constants.DISCOUNT_UPDATED_SUCCESSFULLY, optionalProduct));
        }else{
            return new ResponseEntity<>
                    (new Response(Status.FAILED,
                            Constants.UNAUTHORIZED_REQUEST_CODE,
                            Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public ResponseEntity<Response> updateProductQuantity(Long productId, Integer quantity) {
        Product optionalProduct = productRepository.findById(productId).orElse(null);
        if (optionalProduct==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_NOT_FOUND_CODE, Constants.PRODUCT_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null && optionalProduct.getCreatedBy()!=null && optionalProduct.getCreatedBy().getId() != users.getId()){

            optionalProduct.setQuantity(quantity);
            optionalProduct.setUpdatedAt(LocalDateTime.now());
            productRepository.save(optionalProduct);

            return ResponseEntity.ok(new Response(Constants.QUANTITY_UPDATED_SUCCESSFULLY, optionalProduct));
        }else{
            return new ResponseEntity<>
                    (new Response(Status.FAILED,
                            Constants.UNAUTHORIZED_REQUEST_CODE,
                            Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public ResponseEntity<Response> changeProductStatus(Long productId, ProductStatus status) {
        Product optionalProduct = productRepository.findById(productId).orElse(null);
        if (optionalProduct==null) {
            return new ResponseEntity<>(new Response(Status.FAILED, Constants.PRODUCT_NOT_FOUND_CODE, Constants.PRODUCT_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        Utility utility = new Utility();
        Users users = usersRepository.findByEmail(utility.getCurrentUsername()).orElse(null);
        if(users!=null && optionalProduct.getCreatedBy()!=null && optionalProduct.getCreatedBy().getId() != users.getId()){

            optionalProduct.setStatus(status);
            optionalProduct.setUpdatedAt(LocalDateTime.now());
            productRepository.save(optionalProduct);

            return ResponseEntity.ok(new Response(Constants.STATUS_CHANGED_SUCCESSFULLY, optionalProduct));
        }else{
            return new ResponseEntity<>
                    (new Response(Status.FAILED,
                            Constants.UNAUTHORIZED_REQUEST_CODE,
                            Constants.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }

    }

    public ResponseEntity<Response> fetchProducts(ProductFilterRequest filterRequest) {
        List<Product> products;
        if (filterRequest.getIncludeOutOfStock() == null || !filterRequest.getIncludeOutOfStock()) {
            products = productRepository.findProducts(
                    filterRequest.getName(),
                    filterRequest.getMinPrice(),
                    filterRequest.getMaxPrice(),
                    filterRequest.getCategoryId(),
                    ProductStatus.AVAILABLE
            );
        } else {
            products = productRepository.findProducts(
                    filterRequest.getName(),
                    filterRequest.getMinPrice(),
                    filterRequest.getMaxPrice(),
                    filterRequest.getCategoryId(),
                    null
            );
        }

        List<ProductResponseDTO> productResponses = products.stream().map(product -> {
            List<ProductImageDTO> images = productImageRepository.findByProductId(product.getId())
                    .stream()
                    .map(image -> new ProductImageDTO(image.getId(), image.getImageUrl(), image.getIsPrimary()))
                    .collect(Collectors.toList());

            List<ProductReviewDTO> reviews = productReviewRepository.findByProductId(product.getId())
                    .stream()
                    .map(review -> new ProductReviewDTO(review.getId(), review.getCustomerFullName(),review.getCustomerImage(), review.getReview(), review.getRating(),review.getCreatedAt()))
                    .collect(Collectors.toList());

            List<ProductAttributeDto> attributes = productAttributeRepository.findByProductId(product.getId())
                    .stream()
                    .map(review -> new ProductAttributeDto(review.getId(), review.getAttributeName(), review.getAttributeValue()))
                    .collect(Collectors.toList());

            List<ProductCategoryDto> categories = productCategoryRepository.findCategoryByProductId(product.getId())
                    .stream()
                    .map(category -> new ProductCategoryDto(category.getId(), category.getName(), category.getDescription()))
                    .toList();

            return new ProductResponseDTO(product, images, reviews,attributes,categories);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new Response(Constants.PRODUCTS_FETCH_SUCCESSFULLY, productResponses));
    }

    public ResponseEntity<?> findById(Long id) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        Product product = productRepository.findById(id).orElse(null);
        if(product!=null){
            List<ProductImageDTO> images = productImageRepository.findByProductId(product.getId())
                    .stream()
                    .map(image -> new ProductImageDTO(image.getId(), image.getImageUrl(), image.getIsPrimary()))
                    .collect(Collectors.toList());

            List<ProductReviewDTO> reviews = productReviewRepository.findByProductId(product.getId())
                    .stream()
                    .map(review -> new ProductReviewDTO(review.getId(), review.getCustomerFullName(),review.getCustomerImage(), review.getReview(), review.getRating(),review.getCreatedAt()))
                    .collect(Collectors.toList());

            List<ProductAttributeDto> attributes = productAttributeRepository.findByProductId(product.getId())
                    .stream()
                    .map(review -> new ProductAttributeDto(review.getId(), review.getAttributeName(), review.getAttributeValue()))
                    .collect(Collectors.toList());

            List<ProductCategoryDto> categories = productCategoryRepository.findCategoryByProductId(product.getId())
                    .stream()
                    .map(category -> new ProductCategoryDto(category.getId(), category.getName(), category.getDescription()))
                    .toList();

            responseDTO = new ProductResponseDTO(product, images, reviews,attributes,categories);
            return ResponseEntity.ok(responseDTO);
        }else{
            return new ResponseEntity<Response>(new Response(Status.FAILED,Constants.PRODUCT_NOT_FOUND_CODE,Constants.PRODUCT_NOT_FOUND),HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> findAll() {
        Utility utility = new Utility();
        List<Product> products = productRepository.findByCreatedBy(utility.getCurrentUsername());

        List<ProductResponseDTO> productResponses = products.stream().map(product -> {
            List<ProductImageDTO> images = productImageRepository.findByProductId(product.getId())
                    .stream()
                    .map(image -> new ProductImageDTO(image.getId(), image.getImageUrl(), image.getIsPrimary()))
                    .collect(Collectors.toList());

            List<ProductReviewDTO> reviews = productReviewRepository.findByProductId(product.getId())
                    .stream()
                    .map(review -> new ProductReviewDTO(review.getId(), review.getCustomerFullName(),review.getCustomerImage(), review.getReview(), review.getRating(),review.getCreatedAt()))
                    .collect(Collectors.toList());

            List<ProductAttributeDto> attributes = productAttributeRepository.findByProductId(product.getId())
                    .stream()
                    .map(review -> new ProductAttributeDto(review.getId(), review.getAttributeName(), review.getAttributeValue()))
                    .collect(Collectors.toList());

            List<ProductCategoryDto> categories = productCategoryRepository.findCategoryByProductId(product.getId())
                    .stream()
                    .map(category -> new ProductCategoryDto(category.getId(), category.getName(), category.getDescription()))
                    .toList();

            return new ProductResponseDTO(product, images, reviews,attributes,categories);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new Response(Constants.PRODUCTS_FETCH_SUCCESSFULLY, productResponses));
    }
}