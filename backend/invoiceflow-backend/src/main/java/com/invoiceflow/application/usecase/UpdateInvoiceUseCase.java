package com.invoiceflow.application.usecase;

import com.invoiceflow.application.service.InvoiceWorkflowValidatorService;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.UUID;

@ApplicationScoped
public class UpdateInvoiceUseCase {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceWorkflowValidatorService invoiceWorkflowValidatorService;

    public Invoice execute(UUID invoiceId, UUID companyId, Invoice patch) {
        Invoice existing = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new BadRequestException("Invoice not found"));

        if (!existing.getCompanyId().equals(companyId)) {
            throw new BadRequestException("Invoice does not belong to your company");
        }

        invoiceWorkflowValidatorService.validateCanUpdate(patch.getStatus());

        // apply patch (only overwrite if not null)
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());

        if (patch.getVendorName() != null) existing.setVendorName(patch.getVendorName());
        if (patch.getInvoiceNumber() != null) existing.setInvoiceNumber(patch.getInvoiceNumber());
        if (patch.getInvoiceDate() != null) existing.setInvoiceDate(patch.getInvoiceDate());
        if (patch.getCurrency() != null) existing.setCurrency(patch.getCurrency());

        if (patch.getSubtotalAmount() != null) existing.setSubtotalAmount(patch.getSubtotalAmount());
        if (patch.getTaxAmount() != null) existing.setTaxAmount(patch.getTaxAmount());
        if (patch.getTotalAmount() != null) existing.setTotalAmount(patch.getTotalAmount());

        if (patch.getVatId() != null) existing.setVatId(patch.getVatId());

        return invoiceRepository.update(existing);
    }
}