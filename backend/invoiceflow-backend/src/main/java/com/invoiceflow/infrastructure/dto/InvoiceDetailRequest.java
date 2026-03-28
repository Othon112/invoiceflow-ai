package com.invoiceflow.infrastructure.dto;

import com.invoiceflow.domain.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceDetailRequest {

    private Invoice invoice;
    private DocumentInfo document;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DocumentInfo {
        private UUID id;
        private String originalFilename;
        private String mimeType;
        private String downloadUrl;
    }



}
