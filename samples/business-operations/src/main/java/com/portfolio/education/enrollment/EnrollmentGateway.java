package com.portfolio.education.enrollment;

public interface EnrollmentGateway {
    boolean existsByRequestId(String requestId);

    boolean existsApplicant(String programId, String applicantId);

    Capacity capacityOf(String programId, String organizationId);

    Enrollment saveWithSurvey(EnrollmentRequest request);

    record Capacity(int organizationLimit, int roomLimit, int enrolledCount) {
        public int availableSeats() {
            return Math.min(organizationLimit, roomLimit) - enrolledCount;
        }
    }
}

