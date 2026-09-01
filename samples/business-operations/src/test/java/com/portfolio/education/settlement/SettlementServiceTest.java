package com.portfolio.education.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class SettlementServiceTest {
    private final SettlementService service = new SettlementService(
            Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void approvesSubmittedSettlementWithExchangeRate() {
        Settlement submitted = service.submit(draft());
        Settlement approved = service.approve(submitted, new BigDecimal("1350.50"));

        assertEquals(Settlement.Status.APPROVED, approved.status());
        assertEquals(new BigDecimal("1350.50"), approved.exchangeRate());
        assertEquals(2, approved.history().size());
    }

    @Test
    void rejectionRequiresReasonAndSubmittedState() {
        assertThrows(IllegalStateException.class, () -> service.reject(draft(), "보완 필요"));

        Settlement submitted = service.submit(draft());
        assertThrows(IllegalArgumentException.class, () -> service.reject(submitted, " "));
        assertEquals(Settlement.Status.REJECTED,
                service.reject(submitted, "증빙자료 보완 필요").status());
    }

    private Settlement draft() {
        return new Settlement(
                "settlement-1", new BigDecimal("500000"), null,
                Settlement.Status.DRAFT, List.of()
        );
    }
}
