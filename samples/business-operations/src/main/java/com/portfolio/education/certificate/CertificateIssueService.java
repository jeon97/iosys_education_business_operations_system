package com.portfolio.education.certificate;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

public final class CertificateIssueService {
    private final CertificateRepository repository;
    private final Supplier<String> verificationCode;
    private final Clock clock;

    public CertificateIssueService(CertificateRepository repository,
                                   Supplier<String> verificationCode, Clock clock) {
        this.repository = repository;
        this.verificationCode = verificationCode;
        this.clock = clock;
    }

    public Certificate issue(String requestId, ExamResult result, String issuedBy) {
        Optional<Certificate> previous = repository.findByRequestId(requestId);
        if (previous.isPresent()) return previous.get();
        if (!result.finalized()) throw new IllegalStateException("result is not finalized");
        if (issuedBy == null || issuedBy.isBlank()) throw new IllegalArgumentException("issuer is required");
        String code = verificationCode.get();
        if (code == null || code.length() < 12 || repository.existsByVerificationCode(code)) {
            throw new IllegalStateException("verification code is not usable");
        }
        var certificate = new Certificate(requestId, result.resultId(), code,
                Status.ACTIVE, clock.instant(), null, issuedBy);
        repository.save(certificate);
        return certificate;
    }

    public Certificate revoke(String verificationCode, String revokedBy) {
        Certificate current = repository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new IllegalArgumentException("certificate does not exist"));
        if (current.status() == Status.REVOKED) return current;
        var revoked = new Certificate(current.requestId(), current.resultId(),
                current.verificationCode(), Status.REVOKED, current.issuedAt(),
                clock.instant(), revokedBy);
        repository.save(revoked);
        return revoked;
    }

    public Verification verify(String verificationCode) {
        return repository.findByVerificationCode(verificationCode)
                .map(value -> new Verification(value.status() == Status.ACTIVE,
                        value.resultId(), value.issuedAt()))
                .orElse(new Verification(false, null, null));
    }

    public enum Status { ACTIVE, REVOKED }
    public record ExamResult(String resultId, boolean finalized) {}
    public record Certificate(String requestId, String resultId, String verificationCode,
                              Status status, Instant issuedAt, Instant revokedAt, String processedBy) {}
    public record Verification(boolean valid, String resultId, Instant issuedAt) {}

    public interface CertificateRepository {
        Optional<Certificate> findByRequestId(String requestId);
        Optional<Certificate> findByVerificationCode(String verificationCode);
        boolean existsByVerificationCode(String verificationCode);
        void save(Certificate certificate);
    }
}
