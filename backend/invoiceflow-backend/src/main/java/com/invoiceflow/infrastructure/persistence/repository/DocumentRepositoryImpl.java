package com.invoiceflow.infrastructure.persistence.repository;

import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.repository.DocumentRepository;
import com.invoiceflow.infrastructure.persistence.entity.DocumentEntity;
import com.invoiceflow.infrastructure.persistence.mapper.DocumentMapper;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class DocumentRepositoryImpl implements DocumentRepository, PanacheRepositoryBase<DocumentEntity, UUID> {

    @Override
    @Transactional
    public Document create(Document document) {
        DocumentEntity entity = new DocumentEntity();
        entity.setCompanyId(document.getCompanyId());
        entity.setUploadedByUserId(document.getUploadedByUserId());
        entity.setSha256Hash(document.getSha256Hash());
        entity.setStoragePath(document.getStoragePath());
        entity.setOriginalFilename(document.getOriginalFilename());
        entity.setMimeType(document.getMimeType());
        entity.setSizeBytes(document.getSizeBytes());

        persist(entity);
        flush();
        getEntityManager().refresh(entity);
        return DocumentMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Document update(Document document) {
        DocumentEntity existing = findById(document.getId());
        if (existing == null) {
            throw new IllegalStateException("Document not found");
        }

        existing.setStoragePath(document.getStoragePath());
        existing.setOriginalFilename(document.getOriginalFilename());
        existing.setMimeType(document.getMimeType());
        existing.setSizeBytes(document.getSizeBytes());
        // usually sha256/company/uploader should NOT change

        return DocumentMapper.toDomain(existing);
    }

    @Override
    public Optional<Document> findByCompanyAndHash(UUID companyId, String sha256Hash) {
        DocumentEntity entity = find(
                "companyId = ?1 and sha256Hash = ?2",
                companyId, sha256Hash
        ).firstResult();

        return Optional.ofNullable(DocumentMapper.toDomain(entity));
    }

    @Override
    public Optional<Document> findByDocumentId(UUID id) {
        DocumentEntity entity = findById(id);
        return Optional.ofNullable(DocumentMapper.toDomain(entity));
    }

    @Override
    public List<Document> listByCompany(UUID companyId, int limit, int offset) {
        PanacheQuery<DocumentEntity> q =
                find("companyId", Sort.descending("createdAt"), companyId);

        List<DocumentEntity> entities = q.range(offset, offset + limit - 1).list();
        return entities.stream().map(DocumentMapper::toDomain).toList();
    }

    @Override
    public long countByCompany(UUID companyId) {
        return count("companyId", companyId);

    }
}
