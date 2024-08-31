package com.service.product.repository;

import com.service.product.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    @Query("Select u from ProductReview u where u.product.id = ?1 order by u.createdAt DESC")
    List<ProductReview> findByProductId(Long id);
}