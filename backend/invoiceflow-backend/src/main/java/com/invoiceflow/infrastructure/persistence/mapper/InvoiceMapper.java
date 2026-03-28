package com.invoiceflow.infrastructure.persistence.mapper;

import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.infrastructure.persistence.entity.InvoiceEntity;

public class InvoiceMapper {

    public static Invoice toDomain(InvoiceEntity entity) {
        if (entity == null) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setId(entity.getId());
        invoice.setCompanyId(entity.getCompanyId());
        invoice.setDocumentId(entity.getDocumentId());
        invoice.setCreatedByUserId(entity.getCreatedByUserId());

        invoice.setStatus(entity.getStatus());
        invoice.setVendorName(entity.getVendorName());
        invoice.setInvoiceNumber(entity.getInvoiceNumber());
        invoice.setInvoiceDate(entity.getInvoiceDate());
        invoice.setCurrency(entity.getCurrency());

        invoice.setSubtotalAmount(entity.getSubtotalAmount());
        invoice.setTaxAmount(entity.getTaxAmount());
        invoice.setTotalAmount(entity.getTotalAmount());
        invoice.setVatId(entity.getVatId());

        invoice.setRiskScore(entity.getRiskScore());
        invoice.setRiskLevel(entity.getRiskLevel());
        invoice.setRiskFlagsJson(entity.getRiskFlagsJson());

        invoice.setAiInsight(entity.getAiInsight());

        invoice.setCreatedAt(entity.getCreatedAt());
        invoice.setUpdatedAt(entity.getUpdatedAt());

        return invoice;
    }

    public static InvoiceEntity toEntity(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceEntity entity = new InvoiceEntity();
        entity.setId(invoice.getId());
        entity.setCompanyId(invoice.getCompanyId());
        entity.setDocumentId(invoice.getDocumentId());
        entity.setCreatedByUserId(invoice.getCreatedByUserId());

        entity.setStatus(invoice.getStatus());
        entity.setVendorName(invoice.getVendorName());
        entity.setInvoiceNumber(invoice.getInvoiceNumber());
        entity.setInvoiceDate(invoice.getInvoiceDate());
        entity.setCurrency(invoice.getCurrency());

        entity.setSubtotalAmount(invoice.getSubtotalAmount());
        entity.setTaxAmount(invoice.getTaxAmount());
        entity.setTotalAmount(invoice.getTotalAmount());
        entity.setVatId(invoice.getVatId());

        entity.setRiskScore(invoice.getRiskScore());
        entity.setRiskLevel(invoice.getRiskLevel());
        entity.setRiskFlagsJson(invoice.getRiskFlagsJson());

        invoice.setAiInsight(entity.getAiInsight());

        entity.setCreatedAt(invoice.getCreatedAt());
        entity.setUpdatedAt(invoice.getUpdatedAt());

        return entity;
    }
}