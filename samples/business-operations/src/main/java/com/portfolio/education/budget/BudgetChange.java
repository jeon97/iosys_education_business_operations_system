package com.portfolio.education.budget;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BudgetChange(
        String changeId,
        BigDecimal originalTotal,
        List<BudgetLine> lines,
        Status status,
        List<History> history
) {
    public BudgetChange {
        lines = List.copyOf(lines);
        history = List.copyOf(history);
    }

    public enum Status {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }

    public record BudgetLine(String category, BigDecimal changedAmount) {
    }

    public record History(Status from, Status to, String reason, Instant changedAt) {
    }
}

