package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.repository.DocumentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateDocumentUseCase {

    @Inject
    DocumentRepository documentRepository;

    public Document execute(Document document) {
        return documentRepository.create(document);
    }
}