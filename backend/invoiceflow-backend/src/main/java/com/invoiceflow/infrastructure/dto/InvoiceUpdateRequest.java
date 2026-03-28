package com.invoiceflow.infrastructure.dto;

import java.time.LocalDate;

public class InvoiceUpdateRequest {
    public String status;

    public String vendorName;
    public String invoiceNumber;
    public LocalDate invoiceDate;
    public String currency;

    public Long subtotalAmount;
    public Long taxAmount;
    public Long totalAmount;

    public String vatId;
}