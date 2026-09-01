package com.portfolio.education.enrollment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class EnrollmentServiceTest {
    @Test
    void savesEnrollmentAndSurveyWhenCapacityIsAvailable() {
        FakeGateway gateway = new FakeGateway(new EnrollmentGateway.Capacity(10, 8, 7));
        EnrollmentService service = new EnrollmentService(gateway);

        Enrollment enrollment = service.enroll(request());

        assertEquals("A-0008", enrollment.admissionNumber());
        assertEquals(1, gateway.saveCalls);
    }

    @Test
    void rejectsEnrollmentWhenOrganizationOrRoomIsFull() {
        FakeGateway gateway = new FakeGateway(new EnrollmentGateway.Capacity(10, 8, 8));
        EnrollmentService service = new EnrollmentService(gateway);

        assertThrows(EnrollmentService.CapacityExceededException.class,
                () -> service.enroll(request()));
        assertEquals(0, gateway.saveCalls);
    }

    private EnrollmentRequest request() {
        return new EnrollmentRequest(
                "request-1", "program-1", "organization-1", "applicant-1",
                Map.of("question-1", "answer-1")
        );
    }

    private static final class FakeGateway implements EnrollmentGateway {
        private final Capacity capacity;
        private int saveCalls;

        private FakeGateway(Capacity capacity) {
            this.capacity = capacity;
        }

        @Override
        public boolean existsByRequestId(String requestId) {
            return false;
        }

        @Override
        public boolean existsApplicant(String programId, String applicantId) {
            return false;
        }

        @Override
        public Capacity capacityOf(String programId, String organizationId) {
            return capacity;
        }

        @Override
        public Enrollment saveWithSurvey(EnrollmentRequest request) {
            saveCalls++;
            return new Enrollment("enrollment-1", "A-0008");
        }
    }
}

