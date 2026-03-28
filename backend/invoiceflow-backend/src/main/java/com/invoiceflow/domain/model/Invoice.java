package com.invoiceflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    // Identity
    private UUID id;

    // Ownership
    private UUID companyId;
    private UUID documentId;
    private UUID createdByUserId;

    // Workflow
    private String status;

    // Extracted / reviewed data (nullable at first)
    private String vendorName;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String currency;

    // Money (stored as cents)
    private Long subtotalAmount;
    private Long taxAmount;
    private Long totalAmount;

    // VAT
    private String vatId;

    // Audit
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Risks
    private Integer riskScore;
    private String riskLevel;
    private String riskFlagsJson;

    private String aiInsight;
}