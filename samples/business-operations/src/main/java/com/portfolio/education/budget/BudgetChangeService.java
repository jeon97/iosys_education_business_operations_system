package com.portfolio.education.budget;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;

public final class BudgetChangeService {
    private final Clock clock;

    public BudgetChangeService(Clock clock) {
        this.clock = clock;
    }

    public BudgetChange submit(BudgetChange change) {
        requireStatus(change, BudgetChange.Status.DRAFT);
        validateLines(change);
        return transition(change, BudgetChange.Status.SUBMITTED, "submitted");
    }

    public BudgetChange approve(BudgetChange change) {
        requireStatus(change, BudgetChange.Status.SUBMITTED);
        return transition(change, BudgetChange.Status.APPROVED, "approved");
    }

    public BudgetChange reject(BudgetChange change, String reason) {
        requireStatus(change, BudgetChange.Status.SUBMITTED);
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("rejection reason is required");
        }
        return transition(change, BudgetChange.Status.REJECTED, reason);
    }

    private void validateLines(BudgetChange change) {
        if (change.lines().isEmpty()) {
            throw new IllegalArgumentException("budget lines are required");
        }
        BigDecimal changedTotal = change.lines().stream()
                .map(BudgetChange.BudgetLine::changedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (changedTotal.compareTo(change.originalTotal()) != 0) {
            throw new IllegalArgumentException("changed lines must preserve total budget");
        }
    }

    private void requireStatus(BudgetChange change, BudgetChange.Status expected) {
        if (change.status() != expected) {
            throw new IllegalStateException("invalid budget change status");
        }
    }

    private BudgetChange transition(
            BudgetChange change,
            BudgetChange.Status next,
            String reason
    ) {
        var history = new ArrayList<>(change.history());
        history.add(new BudgetChange.History(change.status(), next, reason, clock.instant()));
        return new BudgetChange(
                change.changeId(), change.originalTotal(), change.lines(), next, history
        );
    }
}

