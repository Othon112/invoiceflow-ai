package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.DocumentRepository;
import com.invoiceflow.domain.repository.InvoiceRepository;
import com.invoiceflow.infrastructure.dto.InvoiceDetailRequest;
import com.invoiceflow.infrastructure.storage.GcsSignedUrlService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URL;
import java.util.UUID;

@ApplicationScoped
public class GetInvoiceDetailUseCase {
    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    GcsSignedUrlService gcsSignedUrlService;

    public InvoiceDetailRequest execute(UUID invoiceId, UUID companyId) {
        Invoice invoice = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new IllegalStateException("Invoice not found"));

        if (!invoice.getCompanyId().equals(companyId)) {
            throw new IllegalStateException("Invoice does not belong to this company");
        }

        Document document = documentRepository.findByDocumentId(invoice.getDocumentId())
                .orElseThrow(() ->  new IllegalStateException("Document not found"));

        URL signedUrl = gcsSignedUrlService.createDownloadUrl(
                document.getStoragePath(), document.getMimeType()
        );

        InvoiceDetailRequest.DocumentInfo docInfo = new InvoiceDetailRequest.DocumentInfo(document.getId(), document.getOriginalFilename(), document.getMimeType(), signedUrl.toString());

        return new InvoiceDetailRequest(invoice, docInfo);

    }
}
