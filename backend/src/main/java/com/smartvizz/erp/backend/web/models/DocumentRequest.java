package com.smartvizz.erp.backend.web.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentRequest(
        @NotNull(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(max = 1000, message = "Description must be up to 1000 characters")
        String description,

        String fileName,

        String fileType,
        
        Long fileSize
) {
}
