package com.financeservice.dto.request;

import com.financeservice.entity.enums.EFinancialReportType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinancialReportSaveRequestDTO(
        EFinancialReportType financialReportType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal totalIncome,
        BigDecimal totalOutcome,
        BigDecimal totalProfit
) {
}
