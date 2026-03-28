package com.invoiceflow.application.usecase;

import com.invoiceflow.application.service.InvoiceWorkflowValidatorService;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.UUID;

@ApplicationScoped
public class RejectInvoiceUseCase {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceWorkflowValidatorService invoiceWorkflowValidatorService;

    public Invoice execute(UUID invoiceId, UUID companyId) {

        Invoice invoice = invoiceRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new BadRequestException("Invoice not found"));

        if (!invoice.getCompanyId().equals(companyId)) {
            throw new BadRequestException("Invoice does not belong to your company");
        }

        invoiceWorkflowValidatorService.validateCanReject(invoice.getStatus());

        invoice.setStatus("REJECTED");

        return invoiceRepository.update(invoice);
    }
}