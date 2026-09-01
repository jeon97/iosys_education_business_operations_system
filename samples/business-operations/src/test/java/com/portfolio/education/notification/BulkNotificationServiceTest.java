package com.portfolio.education.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class BulkNotificationServiceTest {
    @Test
    void continuesAfterInvalidRecipientAndDeliveryFailure() {
        MailSender sender = (recipient, subject, body) -> {
            if (recipient.userId().equals("delivery-error")) {
                throw new IllegalStateException("mail server error");
            }
        };
        BulkNotificationService service = new BulkNotificationService(sender);

        BulkNotificationService.SendResult result = service.send(
                List.of(
                        new Recipient("success", "사용자1", "user1@example.test"),
                        new Recipient("invalid", "사용자2", "invalid-email"),
                        new Recipient("delivery-error", "사용자3", "user3@example.test")
                ),
                "워크숍 안내", "안내 본문"
        );

        assertEquals(List.of("success"), result.succeededUserIds());
        assertEquals(2, result.failures().size());
        assertEquals("email is invalid", result.failures().get(0).reason());
        assertEquals("delivery failed", result.failures().get(1).reason());
    }
}
