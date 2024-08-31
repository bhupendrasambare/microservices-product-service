package com.service.product.repository;

import com.service.product.model.Product;
import com.service.product.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("select (count(p) > 0) from ProductCategory p where p.product.id = ?1 and p.category.id = ?2")
    boolean existByProductAndCategory(Long id, Long id1);

    @Query("select p from ProductCategory p where p.product.id = ?1 and p.category.id = ?2")
    List<ProductCategory> findByProductAndCategory(Long productId, Long categoryId);
}