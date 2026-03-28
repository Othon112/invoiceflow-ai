package com.invoiceflow.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "document_id", nullable = false, unique = true)
    private UUID documentId;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "subtotal_amount")
    private Long subtotalAmount;

    @Column(name = "tax_amount")
    private Long taxAmount;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "vat_id")
    private String vatId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "risk_flags_json")
    private String riskFlagsJson;

    @Column(name = "ai_insight")
    private String aiInsight;
}