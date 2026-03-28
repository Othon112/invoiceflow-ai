package com.invoiceflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceExtractionResult {

    private String vendorName;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String currency;

    private Long subtotalAmount;
    private Long taxAmount;
    private Long totalAmount;

    private String vatId;
}