package com.portfolio.education.settlement;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Settlement(
        String settlementId,
        BigDecimal requestedAmount,
        BigDecimal exchangeRate,
        Status status,
        List<History> history
) {
    public Settlement {
        history = List.copyOf(history);
    }

    public enum Status {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }

    public record History(Status from, Status to, String reason, Instant changedAt) {
    }
}

