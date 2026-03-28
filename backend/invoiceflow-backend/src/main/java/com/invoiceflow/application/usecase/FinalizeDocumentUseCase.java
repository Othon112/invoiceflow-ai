package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.repository.DocumentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FinalizeDocumentUseCase {

    @Inject
    DocumentRepository documentRepository;

    @Transactional
    public Document execute(Document document) {
        Optional<Document> existing =
                documentRepository.findByCompanyAndHash(document.getCompanyId(), document.getSha256Hash());

        if (existing.isPresent()) {
            return existing.get();
        }

        return documentRepository.create(document);
    }
}