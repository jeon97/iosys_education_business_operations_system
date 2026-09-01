package com.portfolio.education.enrollment;

public final class EnrollmentService {
    private final EnrollmentGateway gateway;

    public EnrollmentService(EnrollmentGateway gateway) {
        this.gateway = gateway;
    }

    public Enrollment enroll(EnrollmentRequest request) {
        validate(request);
        if (gateway.existsByRequestId(request.requestId())) {
            throw new IllegalStateException("request was already processed");
        }
        if (gateway.existsApplicant(request.programId(), request.applicantId())) {
            throw new IllegalStateException("applicant is already enrolled");
        }
        EnrollmentGateway.Capacity capacity = gateway.capacityOf(
                request.programId(), request.organizationId()
        );
        if (capacity.availableSeats() <= 0) {
            throw new CapacityExceededException();
        }
        return gateway.saveWithSurvey(request);
    }

    private void validate(EnrollmentRequest request) {
        if (request.requestId() == null || request.requestId().isBlank()) {
            throw new IllegalArgumentException("requestId is required");
        }
        if (request.surveyAnswers().isEmpty()) {
            throw new IllegalArgumentException("survey answers are required");
        }
    }

    public static final class CapacityExceededException extends RuntimeException {
        public CapacityExceededException() {
            super("no seats are available");
        }
    }
}

