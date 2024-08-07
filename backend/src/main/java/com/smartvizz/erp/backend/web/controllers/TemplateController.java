package com.smartvizz.erp.backend.web.controllers;

import com.smartvizz.erp.backend.services.TemplateService;
import com.smartvizz.erp.backend.web.models.TemplateRequest;
import com.smartvizz.erp.backend.web.models.TemplateResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "http://localhost:8888")
public class TemplateController {
    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<Page<TemplateResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "updatedAt,title") String[] sortColumns,
            @RequestParam(defaultValue = "asc,asc") String[] sortDirections,
            @RequestParam(defaultValue = "") String searchBy
    ) {
        Page<TemplateResponse> templates = templateService.fetchAll(page, size, sortColumns, sortDirections, searchBy);

        return ResponseEntity.ok(templates);
    }

    @GetMapping("{id}")
    public ResponseEntity<TemplateResponse> get(@PathVariable Long id) {
        TemplateResponse template = templateService.fetchOne(id);

        return ResponseEntity.ok(template);
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> create(@Valid @RequestBody TemplateRequest request) {
        TemplateResponse createdTemplate = templateService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }

    @PutMapping("{id}")
    public ResponseEntity<TemplateResponse> update(@PathVariable Long id, @Valid @RequestBody TemplateRequest request) {
        TemplateResponse updatedTemplate = templateService.update(id, request);

        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateService.delete(id);

        return ResponseEntity.noContent().build();
    }
}

