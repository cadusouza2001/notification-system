package com.notification.domain.strategy;

import com.notification.domain.model.User;

public interface NotificationStrategy {
    void send(User user, String message);

    String getChannelName(); // "SMS", "E-Mail", or "Push Notification"
}
