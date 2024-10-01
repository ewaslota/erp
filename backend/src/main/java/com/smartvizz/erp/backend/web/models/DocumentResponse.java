package com.smartvizz.erp.backend.web.models;

import com.smartvizz.erp.backend.data.entities.DocumentEntity;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record DocumentResponse (
        Long id,
        String title,
        String description,
        String fileName,
        String filePath,
        String fileType,
        Long fileSize,
        TemplateResponse template,
        List<CategoryResponse> categories,
        UserResponse user,
        Instant createdAt,
        Instant updatedAt
) {
    public DocumentResponse(DocumentEntity entity) {
        this(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getFileName(),
                entity.getFilePath(),
                entity.getFileType(),
                entity.getFileSize(),
                entity.getTemplate() != null ? new TemplateResponse(entity.getTemplate()) : null,
                entity.getCategories() != null ? entity.getCategories().stream()
                        .map(CategoryResponse::new)
                        .collect(Collectors.toList()) : null,
                new UserResponse(entity.getUser()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
