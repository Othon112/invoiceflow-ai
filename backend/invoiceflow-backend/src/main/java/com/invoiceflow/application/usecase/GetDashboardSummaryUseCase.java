package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.InvoiceRepository;
import com.invoiceflow.infrastructure.dto.DashboardSummaryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GetDashboardSummaryUseCase {

    @Inject
    InvoiceRepository invoiceRepository;

    public DashboardSummaryResponse execute(UUID companyId) {
        Long totalSpendThisMonth = invoiceRepository.sumTotalAmountForCurrentMonth(companyId);
        long pendingReviewCount = invoiceRepository.countByCompanyAndStatus(companyId, "NEEDS_REVIEW");
        long approvedCount = invoiceRepository.countByCompanyAndStatus(companyId, "APPROVED");
        long rejectedCount = invoiceRepository.countByCompanyAndStatus(companyId, "REJECTED");
        long highRiskCount = invoiceRepository.countHighRiskByCompany(companyId);

        List<DashboardSummaryResponse.TopVendor> topVendors = new ArrayList<>();
        List<Object[]> vendorRows = invoiceRepository.findTopVendorsByCompany(companyId, 5);
        for (Object[] row : vendorRows) {
            String vendorName = (String) row[0];
            Long totalAmount = row[1] == null ? 0L : ((Number) row[1]).longValue();

            topVendors.add(new DashboardSummaryResponse.TopVendor(
                    vendorName,
                    totalAmount
            ));
        }

        List<DashboardSummaryResponse.RecentAlert> recentAlerts = new ArrayList<>();
        List<Invoice> riskyInvoices = invoiceRepository.findRecentRiskyInvoicesByCompany(companyId, 5);
        for (Invoice invoice : riskyInvoices) {
            recentAlerts.add(new DashboardSummaryResponse.RecentAlert(
                    invoice.getId(),
                    invoice.getVendorName(),
                    invoice.getRiskLevel(),
                    invoice.getAiInsight()
            ));
        }

        return new DashboardSummaryResponse(
                totalSpendThisMonth,
                pendingReviewCount,
                approvedCount,
                rejectedCount,
                highRiskCount,
                topVendors,
                recentAlerts
        );
    }
}