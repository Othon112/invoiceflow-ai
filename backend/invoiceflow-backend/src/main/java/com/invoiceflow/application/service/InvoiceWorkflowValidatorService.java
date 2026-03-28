package com.invoiceflow.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.util.Set;

@ApplicationScoped
public class InvoiceWorkflowValidatorService {
    public static final Set<String> EXTRACT_ALLOWED = Set.of("UPLOADED", "EXTRACTION_FAILED");

    public void validateCanExtract(String status){
        if (!EXTRACT_ALLOWED.contains(status)) {
            throw new BadRequestException("Cannot extract invoice in status:  " + status);
        }
    }

    public void validateCanApprove(String status){
        if (!"NEEDS_REVIEW".equals(status)) {
            throw new BadRequestException("Only invoices in NEEDS_REVIEW can be approved");
        }
    }

    public void validateCanReject(String status){
        if (!"NEEDS_REVIEW".equals(status)) {
            throw new BadRequestException("Only invoices in NEEDS_REVIEW can be rejected");
        }
    }

    public void validateCanUpdate(String status){
        if ("APPROVED".equals(status)) {
            throw new BadRequestException("Cannot update invoice in status: APPROVED");
        }
    }

    public void validateCanAnalyze(String status) {
        if (!"NEEDS_REVIEW".equals(status) && !"APPROVED".equals(status)) {
            throw new jakarta.ws.rs.BadRequestException(
                    "Invoice can only be analyzed in NEEDS_REVIEW or APPROVED"
            );
        }
    }
}
