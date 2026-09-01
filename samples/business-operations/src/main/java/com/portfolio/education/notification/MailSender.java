package com.portfolio.education.notification;

public interface MailSender {
    void send(Recipient recipient, String subject, String body);
}

