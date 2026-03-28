package com.invoiceflow.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceflow.application.service.InvoiceInsightService;
import com.invoiceflow.application.service.InvoiceWorkflowValidatorService;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AnalyzeInvoiceUseCase {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceWorkflowValidatorService workflowValidator;

    @Inject
    InvoiceInsightService insightService;

    public Invoice execute(UUID invoiceId, UUID companyId) {
        Invoice invoice = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        if (!invoice.getCompanyId().equals(companyId)) {
            throw new ForbiddenException("You cannot access this invoice");
        }

        workflowValidator.validateCanAnalyze(invoice.getStatus());

        List<String> flags = new ArrayList<>();
        int score = 0;

        String vendorName = invoice.getVendorName();
        Long totalAmount = invoice.getTotalAmount();

        if (vendorName != null && !vendorName.isBlank()) {
            long vendorCount = invoiceRepository.countByCompanyAndVendor(companyId, vendorName);

            if (vendorCount <= 1) {
                flags.add("New vendor");
                score += 15;
            }

            Double avgAmount = invoiceRepository.averageTotalAmountByCompanyAndVendor(companyId, vendorName);
            if (avgAmount != null && totalAmount != null && avgAmount > 0) {
                if (totalAmount > avgAmount * 2) {
                    flags.add("Unusual amount");
                    score += 25;
                }
            }

            if (totalAmount != null) {
                boolean duplicate = invoiceRepository.existsPossibleDuplicate(
                        companyId,
                        vendorName,
                        totalAmount,
                        invoice.getId()
                );

                if (duplicate) {
                    flags.add("Possible duplicate");
                    score += 50;
                }
            }
        }

        String riskLevel;
        if (score >= 60) {
            riskLevel = "HIGH";
        } else if (score >= 25) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        try {
            String flagsJson = new ObjectMapper().writeValueAsString(flags);

            String insight = insightService.generateInsight(
                    invoice.getVendorName(),
                    invoice.getTotalAmount(),
                    riskLevel,
                    flagsJson
            );
            invoice.setRiskScore(score);
            invoice.setRiskLevel(riskLevel);
            invoice.setRiskFlagsJson(flagsJson);
            invoice.setAiInsight(insight);

            return invoiceRepository.update(invoice);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize risk flags", e);
        }
    }
}