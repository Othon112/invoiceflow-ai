package com.invoiceflow.infrastructure.persistence.mapper;

import com.invoiceflow.domain.model.Document;
import com.invoiceflow.infrastructure.persistence.entity.DocumentEntity;

public class DocumentMapper {
    public static Document toDomain(DocumentEntity documentEntity) {
        if (documentEntity == null) {
            return null;
        }
        Document document = new Document();
        document.setId(documentEntity.getId());
        document.setCompanyId(documentEntity.getCompanyId());
        document.setUploadedByUserId(documentEntity.getUploadedByUserId());
        document.setStoragePath(documentEntity.getStoragePath());
        document.setOriginalFilename(documentEntity.getOriginalFilename());
        document.setMimeType(documentEntity.getMimeType());
        document.setSizeBytes(documentEntity.getSizeBytes() == null ? 0L : documentEntity.getSizeBytes());
        document.setSha256Hash(documentEntity.getSha256Hash());
        document.setCreatedAt(documentEntity.getCreatedAt());
        return document;
    }
    public static DocumentEntity toEntity(Document document) {
        if (document == null) {
            return null;
        }

        DocumentEntity entity = new DocumentEntity();
        entity.setCompanyId(document.getCompanyId());
        entity.setUploadedByUserId(document.getUploadedByUserId());
        entity.setStoragePath(document.getStoragePath());
        entity.setOriginalFilename(document.getOriginalFilename());
        entity.setMimeType(document.getMimeType());
        entity.setSizeBytes(document.getSizeBytes());
        entity.setSha256Hash(document.getSha256Hash());
        return entity;
    }
}
