package com.businessapi.dto.request;

import com.businessapi.entity.enums.EBudgetCategory;

import java.math.BigDecimal;

public record BudgetSaveRequestDTO(
        Long departmentId,
        BigDecimal subAmount,
        EBudgetCategory budgetCategory,
        String description
) {
}