package com.portfolio.education.budget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class BudgetChangeServiceTest {
    private final BudgetChangeService service = new BudgetChangeService(
            Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void recordsHistoryWhenSubmittedAndApproved() {
        BudgetChange submitted = service.submit(draft());
        BudgetChange approved = service.approve(submitted);

        assertEquals(BudgetChange.Status.APPROVED, approved.status());
        assertEquals(2, approved.history().size());
        assertEquals(BudgetChange.Status.SUBMITTED, approved.history().get(1).from());
    }

    @Test
    void requiresReasonWhenRejected() {
        BudgetChange submitted = service.submit(draft());

        assertThrows(IllegalArgumentException.class, () -> service.reject(submitted, " "));
    }

    @Test
    void rejectsInvalidStateTransition() {
        assertThrows(IllegalStateException.class, () -> service.approve(draft()));
    }

    private BudgetChange draft() {
        return new BudgetChange(
                "change-1", new BigDecimal("1000000"),
                List.of(
                        new BudgetChange.BudgetLine("training", new BigDecimal("600000")),
                        new BudgetChange.BudgetLine("materials", new BigDecimal("400000"))
                ),
                BudgetChange.Status.DRAFT, List.of()
        );
    }
}

