package com.invoiceflow.application.service;

import com.invoiceflow.domain.model.Invoice;

public interface InvoiceExtractionService {
    ExtractedInvoiceData extract(byte[] fileBytes, String mimeType);

    class ExtractedInvoiceData {
        public String vendorName;
        public String invoiceNumber;
        public java.time.LocalDate invoiceDate;
        public String currency;
        public Long subtotalAmount;
        public Long taxAmount;
        public Long totalAmount;
        public String vatId;
    }
}