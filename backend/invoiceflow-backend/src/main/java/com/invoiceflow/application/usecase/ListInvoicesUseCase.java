package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListInvoicesUseCase {

    @Inject
    InvoiceRepository invoiceRepository;

    public Result execute(UUID companyId, int limit, int offset) {
        long total = invoiceRepository.countByCompany(companyId);
        List<Invoice> items = invoiceRepository.listByCompany(companyId, limit, offset);
        boolean hasNext = (offset + items.size()) < total;
        return new Result(items, limit, offset, total, hasNext);
    }

    public record Result(
            List<Invoice> items,
            int limit,
            int offset,
            long total,
            boolean hasNext
    ) {}
}