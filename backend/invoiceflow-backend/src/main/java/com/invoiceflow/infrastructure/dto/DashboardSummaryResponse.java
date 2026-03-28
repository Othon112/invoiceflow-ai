package com.invoiceflow.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DashboardSummaryResponse {
    private Long totalSpendThisMonth;
    private Long pendingReviewCount;
    private Long approvedCount;
    private Long rejectedCount;
    private Long highRiskCount;
    private List<TopVendor> topVendors;
    private List<RecentAlert> recentAlerts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopVendor {
        private String vendorName;
        private Long totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentAlert {
        private UUID invoiceId;
        private String vendorName;
        private String riskLevel;
        private String aiInsight;
    }


}
