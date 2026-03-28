package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.DocumentRepository;
import com.invoiceflow.domain.repository.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.UUID;

@ApplicationScoped
public class CreateInvoiceFromDocumentUseCase {

    @Inject
    DocumentRepository documentRepository;

    @Inject
    InvoiceRepository invoiceRepository;

    public Invoice execute(UUID documentId, UUID userId, UUID companyId) {

        Document document = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new BadRequestException("Document not found"));

        if (!document.getCompanyId().equals(companyId)) {
            throw new BadRequestException("Document does not belong to your company");
        }

        invoiceRepository.findByDocumentId(documentId)
                .ifPresent(i -> {
                    throw new BadRequestException("Invoice already exists for this document");
                });

        Invoice invoice = new Invoice();
        invoice.setCompanyId(companyId);
        invoice.setDocumentId(documentId);
        invoice.setCreatedByUserId(userId);
        invoice.setStatus("UPLOADED");

        return invoiceRepository.create(invoice);
    }
}