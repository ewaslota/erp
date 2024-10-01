package com.smartvizz.erp.backend.web.controllers;

import com.smartvizz.erp.backend.services.DocumentService;
import com.smartvizz.erp.backend.web.models.DocumentRequest;
import com.smartvizz.erp.backend.web.models.DocumentResponse;
import com.smartvizz.erp.backend.web.models.PageDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = {
        "http://localhost:8889",
        "http://localhost:4200",
        "http://162.55.215.13:8889",
        "http://162.55.215.13:4200"
})
public class DocumentController {

    private final DocumentService documentService;
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<PageDTO<DocumentResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "updatedAt,title") String[] sortColumns,
            @RequestParam(defaultValue = "asc,asc") String[] sortDirections,
            @RequestParam(defaultValue = "") String searchBy,
            @AuthenticationPrincipal User user
    ) {
        PageDTO<DocumentResponse> documents = documentService.fetchAll(
                page,
                size,
                sortColumns,
                sortDirections,
                searchBy,
                user
        );


        return ResponseEntity.ok(documents);
    }

    @GetMapping("{id}")
    public ResponseEntity<DocumentResponse> get(@PathVariable Long id, @AuthenticationPrincipal User user) {
        DocumentResponse document = documentService.fetchOne(id, user);

        return ResponseEntity.ok(document);
    }

    @GetMapping("{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            DocumentResponse document = documentService.fetchOne(id, user);

            if (document.filePath() == null || document.fileType() == null) {
                return ResponseEntity.notFound().build();
            }

            String filePathString = document.filePath();
            Path filePath = Paths.get(filePathString);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String mimeType = document.fileType();

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\""
                        )
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentResponse> create(
            @Valid @ModelAttribute DocumentRequest request,
            @AuthenticationPrincipal User user
    ) {
        DocumentResponse createdDocument = documentService.create(request, user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
    }

    @PutMapping(value = "{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentResponse> update(
            @PathVariable Long id,
            @Valid @ModelAttribute DocumentRequest request,
            @AuthenticationPrincipal User user
            ) {
        DocumentResponse updatedDocument = documentService.update(id, request, user);
        
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        documentService.delete(id, user);
        
        return ResponseEntity.noContent().build();
    }
}

