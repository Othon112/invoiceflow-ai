package com.invoiceflow.domain.repository;

import com.invoiceflow.domain.model.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice create(Invoice invoice);

    Optional<Invoice> findByDocumentId(UUID documentId);

    List<Invoice> listByCompany(UUID companyId, int limit, int offset);

    long countByCompany(UUID companyId);
    Optional<Invoice> findByInvoiceId(UUID invoiceId);

    Invoice update(Invoice invoice);

    long countByCompanyAndVendor(UUID companyId, String vendorName);

    Double averageTotalAmountByCompanyAndVendor(UUID companyId, String vendorName);

    boolean existsPossibleDuplicate(UUID companyId, String vendorName, Long totalAmount, UUID excludeInvoiceId);

    Long sumTotalAmountForCurrentMonth(UUID companyId);

    long countByCompanyAndStatus(UUID companyId, String status);

    long countHighRiskByCompany(UUID companyId);

    List<Object[]> findTopVendorsByCompany(UUID companyId, int limit);

    List<Invoice> findRecentRiskyInvoicesByCompany(UUID companyId, int limit);
}