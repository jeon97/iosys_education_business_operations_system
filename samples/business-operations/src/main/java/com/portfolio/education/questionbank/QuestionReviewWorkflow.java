package com.portfolio.education.questionbank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class QuestionReviewWorkflow {
    private final Map<String, ReviewItem> items = new LinkedHashMap<>();
    private Status status = Status.DRAFT;

    public void assign(String questionId, String reviewerId) {
        requireStatus(Status.DRAFT);
        if (items.putIfAbsent(questionId, new ReviewItem(reviewerId)) != null) {
            throw new IllegalArgumentException("question already assigned");
        }
    }

    public void open() {
        requireStatus(Status.DRAFT);
        if (items.isEmpty()) throw new IllegalStateException("review item is required");
        status = Status.IN_PROGRESS;
    }

    public void decide(String questionId, String reviewerId, Decision decision) {
        requireStatus(Status.IN_PROGRESS);
        ReviewItem item = requiredItem(questionId);
        if (!item.reviewerId.equals(reviewerId)) {
            throw new SecurityException("not assigned reviewer");
        }
        item.decision = Objects.requireNonNull(decision);
        item.revisionCompleted = decision != Decision.REVISION_REQUIRED;
    }

    public void completeRevision(String questionId) {
        requireStatus(Status.IN_PROGRESS);
        ReviewItem item = requiredItem(questionId);
        if (item.decision != Decision.REVISION_REQUIRED) {
            throw new IllegalStateException("revision was not requested");
        }
        item.revisionCompleted = true;
    }

    public void complete() {
        requireStatus(Status.IN_PROGRESS);
        boolean incomplete = items.values().stream()
                .anyMatch(item -> item.decision == null || !item.revisionCompleted);
        if (incomplete) throw new IllegalStateException("all reviews must be completed");
        status = Status.COMPLETED;
    }

    public void reopen() {
        requireStatus(Status.COMPLETED);
        status = Status.IN_PROGRESS;
    }

    public int completedCount() {
        return (int) items.values().stream()
                .filter(item -> item.decision != null && item.revisionCompleted)
                .count();
    }

    public int totalCount() { return items.size(); }
    public Status status() { return status; }

    private ReviewItem requiredItem(String questionId) {
        ReviewItem item = items.get(questionId);
        if (item == null) throw new IllegalArgumentException("question is not in the plan");
        return item;
    }

    private void requireStatus(Status expected) {
        if (status != expected) throw new IllegalStateException("invalid workflow status");
    }

    public enum Status { DRAFT, IN_PROGRESS, COMPLETED }
    public enum Decision { ACCEPTED, REJECTED, REVISION_REQUIRED, DISCARDED }

    private static final class ReviewItem {
        private final String reviewerId;
        private Decision decision;
        private boolean revisionCompleted;

        private ReviewItem(String reviewerId) {
            this.reviewerId = Objects.requireNonNull(reviewerId);
        }
    }
}

