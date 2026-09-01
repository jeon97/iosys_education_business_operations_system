package com.portfolio.education.notification;

import java.util.ArrayList;
import java.util.List;

public final class BulkNotificationService {
    private final MailSender sender;

    public BulkNotificationService(MailSender sender) {
        this.sender = sender;
    }

    public SendResult send(List<Recipient> recipients, String subject, String body) {
        if (subject == null || subject.isBlank() || body == null || body.isBlank()) {
            throw new IllegalArgumentException("subject and body are required");
        }

        List<String> succeeded = new ArrayList<>();
        List<Failure> failed = new ArrayList<>();
        for (Recipient recipient : recipients) {
            String invalidReason = validate(recipient);
            if (invalidReason != null) {
                failed.add(new Failure(recipient.userId(), invalidReason));
                continue;
            }
            try {
                sender.send(recipient, subject, body);
                succeeded.add(recipient.userId());
            } catch (RuntimeException exception) {
                failed.add(new Failure(recipient.userId(), "delivery failed"));
            }
        }
        return new SendResult(succeeded, failed);
    }

    private String validate(Recipient recipient) {
        if (recipient.userId() == null || recipient.userId().isBlank()) {
            return "userId is missing";
        }
        if (recipient.name() == null || recipient.name().isBlank()) {
            return "name is missing";
        }
        if (recipient.email() == null || !recipient.email().contains("@")) {
            return "email is invalid";
        }
        return null;
    }

    public record Failure(String userId, String reason) {
    }

    public record SendResult(List<String> succeededUserIds, List<Failure> failures) {
        public SendResult {
            succeededUserIds = List.copyOf(succeededUserIds);
            failures = List.copyOf(failures);
        }
    }
}

