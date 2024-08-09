package com.smartvizz.erp.backend.services;

import com.smartvizz.erp.backend.data.entities.DocumentEntity;
import com.smartvizz.erp.backend.data.repositories.DocumentRepository;
import com.smartvizz.erp.backend.data.specifications.DocumentSpecifications;
import com.smartvizz.erp.backend.web.models.DocumentRequest;
import com.smartvizz.erp.backend.web.models.DocumentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private final Path fileStorageLocation;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        this.fileStorageLocation = Paths.get("uploaded-files").toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Page<DocumentResponse> fetchAll(
            int page,
            int size,
            String[] sortColumns,
            String[] sortDirections,
            String searchBy
    ) {
        logger.debug("sortColumns: " + Arrays.toString(sortColumns) + ", sortDirections: " + Arrays.toString(sortDirections));
        ArrayList<Sort.Order> sortOrders = new ArrayList<>();

        for (int i = 0; i < sortColumns.length; i++) {
            String sortColumn = sortColumns[i];
            Sort.Direction sortDirection =
                    sortDirections.length > i && sortDirections[i].equalsIgnoreCase("desc")
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC;

            sortOrders.add(new Sort.Order(sortDirection, sortColumn));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        return documentRepository.findAll(DocumentSpecifications.searchDocument(searchBy), pageable)
                .map(DocumentResponse::new);
    }

    public DocumentResponse fetchOne(Long id) {
        return documentRepository.findById(id)
                .map(DocumentResponse::new)
                .orElseThrow(NotFoundException::new);
    }

    public DocumentResponse create(DocumentRequest request, MultipartFile file) {
        DocumentEntity documentEntity = new DocumentEntity(request.title(), request.description());
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String fileType = file.getContentType();
                Long fileSize = file.getSize();

                documentEntity.setFileName(fileName);
                documentEntity.setFileType(fileType);
                documentEntity.setFileSize(fileSize);

                Path targetLocation = fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
            }
        }
        DocumentEntity savedDocument = documentRepository.save(documentEntity);

        return new DocumentResponse(savedDocument);
    }

    public DocumentResponse update(Long id, DocumentRequest request, MultipartFile file) {
        DocumentEntity documentEntity = documentRepository.getReferenceById(id);
        documentEntity.setTitle(request.title());
        documentEntity.setDescription(request.description());

        if (file != null && !file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String fileType = file.getContentType();
                Long fileSize = file.getSize();

                documentEntity.setFileName(fileName);
                documentEntity.setFileType(fileType);
                documentEntity.setFileSize(fileSize);

                Path targetLocation = fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
            }
        }

        DocumentEntity updatedDocument = documentRepository.save(documentEntity);

        return new DocumentResponse(updatedDocument);
    }

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }
}
