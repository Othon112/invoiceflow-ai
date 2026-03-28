package com.invoiceflow.domain.repository;

import com.invoiceflow.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Document create(Document document);
    Document update(Document document);
    Optional<Document> findByCompanyAndHash(UUID companyId, String sha256Hash);
    Optional<Document> findByDocumentId(UUID id);
    List<Document> listByCompany(UUID companyId, int limit, int offset);
    long countByCompany(UUID companyId);

}
