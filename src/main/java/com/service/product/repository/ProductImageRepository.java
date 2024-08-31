package com.service.product.repository;

import com.service.product.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("Select u from ProductImage u where u.product.id = ?1")
    List<ProductImage> findByProductId(Long id);
}