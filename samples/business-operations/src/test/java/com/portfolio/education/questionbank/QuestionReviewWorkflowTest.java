package com.portfolio.education.questionbank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionReviewWorkflowTest {
    @Test
    void revisionItemMustBeFixedBeforeCompletion() {
        var workflow = new QuestionReviewWorkflow();
        workflow.assign("question-1", "reviewer-1");
        workflow.open();
        workflow.decide("question-1", "reviewer-1",
                QuestionReviewWorkflow.Decision.REVISION_REQUIRED);

        assertThrows(IllegalStateException.class, workflow::complete);

        workflow.completeRevision("question-1");
        workflow.complete();

        assertEquals(QuestionReviewWorkflow.Status.COMPLETED, workflow.status());
        assertEquals(1, workflow.completedCount());
    }

    @Test
    void unassignedReviewerCannotDecide() {
        var workflow = new QuestionReviewWorkflow();
        workflow.assign("question-1", "reviewer-1");
        workflow.open();

        assertThrows(SecurityException.class, () -> workflow.decide(
                "question-1", "reviewer-2", QuestionReviewWorkflow.Decision.ACCEPTED));
    }
}

