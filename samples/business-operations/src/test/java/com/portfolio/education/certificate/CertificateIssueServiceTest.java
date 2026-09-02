package com.portfolio.education.certificate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CertificateIssueServiceTest {
    private final MemoryRepository repository = new MemoryRepository();
    private final CertificateIssueService service = new CertificateIssueService(
            repository, () -> "public-code-123456", Clock.fixed(
                    Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void issuesOnceForRepeatedRequest() {
        var first = service.issue("request-1", new CertificateIssueService.ExamResult("result-1", true), "operator");
        var repeated = service.issue("request-1", new CertificateIssueService.ExamResult("result-1", true), "operator");
        assertSame(first, repeated);
        assertTrue(service.verify(first.verificationCode()).valid());
    }

    @Test
    void revocationInvalidatesPublicVerification() {
        var issued = service.issue("request-1", new CertificateIssueService.ExamResult("result-1", true), "operator");
        service.revoke(issued.verificationCode(), "operator");
        assertFalse(service.verify(issued.verificationCode()).valid());
    }

    @Test
    void rejectsUnfinalizedResult() {
        assertThrows(IllegalStateException.class, () -> service.issue("request-1",
                new CertificateIssueService.ExamResult("result-1", false), "operator"));
    }

    private static final class MemoryRepository implements CertificateIssueService.CertificateRepository {
        private final Map<String, CertificateIssueService.Certificate> byRequest = new HashMap<>();
        private final Map<String, CertificateIssueService.Certificate> byCode = new HashMap<>();
        public Optional<CertificateIssueService.Certificate> findByRequestId(String id) { return Optional.ofNullable(byRequest.get(id)); }
        public Optional<CertificateIssueService.Certificate> findByVerificationCode(String code) { return Optional.ofNullable(byCode.get(code)); }
        public boolean existsByVerificationCode(String code) { return byCode.containsKey(code); }
        public void save(CertificateIssueService.Certificate value) { byRequest.put(value.requestId(), value); byCode.put(value.verificationCode(), value); }
    }
}
