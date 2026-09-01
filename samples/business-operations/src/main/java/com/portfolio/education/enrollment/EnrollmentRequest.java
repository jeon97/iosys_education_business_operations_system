package com.portfolio.education.enrollment;

import java.util.Map;

public record EnrollmentRequest(
        String requestId,
        String programId,
        String organizationId,
        String applicantId,
        Map<String, String> surveyAnswers
) {
    public EnrollmentRequest {
        surveyAnswers = Map.copyOf(surveyAnswers);
    }
}

