package com.portfolio.education.settlement;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;

public final class SettlementService {
    private final Clock clock;

    public SettlementService(Clock clock) {
        this.clock = clock;
    }

    public Settlement submit(Settlement settlement) {
        requireStatus(settlement, Settlement.Status.DRAFT);
        if (settlement.requestedAmount() == null || settlement.requestedAmount().signum() < 0) {
            throw new IllegalArgumentException("requested amount cannot be negative");
        }
        return transition(settlement, Settlement.Status.SUBMITTED, "submitted", null);
    }

    public Settlement approve(Settlement settlement, BigDecimal exchangeRate) {
        requireStatus(settlement, Settlement.Status.SUBMITTED);
        if (exchangeRate == null || exchangeRate.signum() <= 0) {
            throw new IllegalArgumentException("positive exchange rate is required");
        }
        return transition(settlement, Settlement.Status.APPROVED, "approved", exchangeRate);
    }

    public Settlement reject(Settlement settlement, String reason) {
        requireStatus(settlement, Settlement.Status.SUBMITTED);
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("rejection reason is required");
        }
        return transition(settlement, Settlement.Status.REJECTED, reason, null);
    }

    private Settlement transition(
            Settlement settlement,
            Settlement.Status next,
            String reason,
            BigDecimal exchangeRate
    ) {
        var history = new ArrayList<>(settlement.history());
        history.add(new Settlement.History(
                settlement.status(), next, reason, clock.instant()
        ));
        return new Settlement(
                settlement.settlementId(), settlement.requestedAmount(),
                exchangeRate == null ? settlement.exchangeRate() : exchangeRate,
                next, history
        );
    }

    private void requireStatus(Settlement settlement, Settlement.Status expected) {
        if (settlement.status() != expected) {
            throw new IllegalStateException("invalid settlement status");
        }
    }
}

