package org.example.domain.strategy;

import org.example.infrastructure.persistence.entity.User;

public interface NotificationStrategy {
    void send(User user, String message);
    String getChannelName(); // "SMS", "E-Mail", or "Push Notification"
}
