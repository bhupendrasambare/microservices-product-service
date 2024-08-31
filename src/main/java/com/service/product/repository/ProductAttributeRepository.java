package com.service.product.repository;

import com.service.product.model.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    @Query("Select u from ProductAttribute u where u.productId = ?1")
    List<ProductAttribute> findByProductId(Long id);
}