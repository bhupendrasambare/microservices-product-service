/**
 * author @bhupendrasambare
 * Date   :31/08/24
 * Time   :1:18 am
 * Project:product service
 **/
package com.service.product.service;

import com.service.product.config.Constants;
import com.service.product.dto.Status;
import com.service.product.dto.request.CategoryRequest;
import com.service.product.dto.response.Response;
import com.service.product.model.Category;
import com.service.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<Response> createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category = categoryRepository.save(category);
        return ResponseEntity.status(201).body(new Response(Constants.CATEGORY_CREATED_SUCCESSFULLY, category));
    }

    public ResponseEntity<Response> getAllCategories() {
        return ResponseEntity.ok(new Response(Constants.CATEGORIES_FETCHED_SUCCESSFULLY, categoryRepository.findAll()));
    }

    @Transactional
    public ResponseEntity<Response> updateCategory(Long id, CategoryRequest categoryRequest) {
        ResponseEntity<Response> response = null;
        Category existingCategory = categoryRepository.findById(id)
                .orElse(null);
        if(existingCategory!=null){

            if (categoryRequest.name() != null && !categoryRequest.name().isEmpty()) {
                existingCategory.setName(categoryRequest.name());
            }
            if (categoryRequest.description() != null && !categoryRequest.description().isEmpty()) {
                existingCategory.setDescription(categoryRequest.description());
            }
            existingCategory.setUpdatedAt(LocalDateTime.now());

            existingCategory =  categoryRepository.save(existingCategory);
            response = ResponseEntity.ok(new Response(Constants.CATEGORY_UPDATED_SUCCESSFULLY, existingCategory));
        }else{
            response = new ResponseEntity<Response>(new Response(Status.FAILED,Constants.CATEGORY_NOT_FOUND_CODE,Constants.CATEGORY_NOT_FOUND),HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
