package com.invoiceflow.application.usecase;

import com.invoiceflow.application.service.InvoiceExtractionService;
import com.invoiceflow.application.service.InvoiceWorkflowValidatorService;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.DocumentRepository;
import com.invoiceflow.domain.repository.InvoiceRepository;
import com.invoiceflow.infrastructure.storage.GcsObjectReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class ExtractInvoiceUseCase {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    GcsObjectReader gcsObjectReader;

    @Inject
    InvoiceExtractionService extractionService;

    @Inject
    InvoiceWorkflowValidatorService invoiceWorkflowValidatorService;

    @Transactional
    public Invoice execute(UUID invoiceId, UUID companyId) {

        System.out.println("=== START EXTRACTION ===");
        System.out.println("Invoice ID: " + invoiceId);
        System.out.println("Company ID: " + companyId);

        Invoice invoice = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new BadRequestException("Invoice not found"));

        if (!invoice.getCompanyId().equals(companyId)) {
            throw new BadRequestException("Invoice does not belong to your company");
        }

        invoiceWorkflowValidatorService.validateCanExtract(invoice.getStatus());

        // 1) marca en progreso
        invoice.setStatus("EXTRACTION_IN_PROGRESS");
        invoiceRepository.update(invoice);

        try {
            // 2) obtener documento + bytes del PDF
            var doc = documentRepository.findByDocumentId(invoice.getDocumentId())
                    .orElseThrow(() -> new IllegalStateException("Document not found"));

            System.out.println("Document path: " + doc.getStoragePath());

            byte[] bytes = gcsObjectReader.readBytes(doc.getStoragePath());
            String contentType = gcsObjectReader.readContentType(doc.getStoragePath());

            System.out.println("File size: " + bytes.length);
            System.out.println("Content type: " + contentType);

            // DEBUG: ver qué texto estamos enviando
            String debugText = new String(bytes);
            System.out.println("=== RAW TEXT FROM FILE ===");
            System.out.println(debugText.substring(0, Math.min(500, debugText.length())));

            // 3) Extraer
            var extracted = extractionService.extract(bytes, contentType);

            System.out.println("=== EXTRACTION RESULT ===");
            System.out.println("Vendor: " + extracted.vendorName);
            System.out.println("InvoiceNumber: " + extracted.invoiceNumber);
            System.out.println("Total: " + extracted.totalAmount);

            // 4) guardar resultado
            invoice.setVendorName(extracted.vendorName);
            invoice.setInvoiceNumber(extracted.invoiceNumber);
            invoice.setInvoiceDate(extracted.invoiceDate);
            invoice.setCurrency(extracted.currency);
            invoice.setSubtotalAmount(extracted.subtotalAmount);
            invoice.setTaxAmount(extracted.taxAmount);
            invoice.setTotalAmount(extracted.totalAmount);
            invoice.setVatId(extracted.vatId);

            invoice.setStatus("NEEDS_REVIEW");

            System.out.println("=== EXTRACTION SUCCESS ===");

            return invoiceRepository.update(invoice);

        } catch (Exception e) {
            System.out.println("=== EXTRACTION ERROR ===");
            e.printStackTrace();

            invoice.setStatus("EXTRACTION_FAILED");
            invoiceRepository.update(invoice);

            throw new IllegalStateException("Extraction failed: " + e.getMessage(), e);
        }
    }
}