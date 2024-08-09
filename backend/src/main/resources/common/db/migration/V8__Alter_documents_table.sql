ALTER TABLE documents
    ADD COLUMN file_name VARCHAR(255) AFTER description,
    ADD COLUMN file_type VARCHAR(255) AFTER file_name,
    ADD COLUMN file_size BIGINT AFTER file_type;
