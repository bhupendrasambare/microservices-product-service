package com.service.product.dto.dto;
import java.time.LocalDateTime;

public record ProductReviewDTO(Long id, String reviewerName,String userImage, String reviewText, Integer rating,LocalDateTime createdAt) {
}
