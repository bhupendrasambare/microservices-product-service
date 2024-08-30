package com.service.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(@NotBlank(message = "Name is mandatory")
                              @Size(max = 255, message = "Name must be less than 255 characters")
                              String name,

                              @Size(max = 1000, message = "Description must be less than 1000 characters")
                              String description
) {}