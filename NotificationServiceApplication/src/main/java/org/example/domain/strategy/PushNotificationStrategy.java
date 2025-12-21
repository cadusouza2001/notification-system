package org.example.domain.strategy;

import org.example.infrastructure.persistence.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationStrategy implements NotificationStrategy {
    private static final Logger log = LoggerFactory.getLogger(PushNotificationStrategy.class);

    @Override
    public void send(User user, String message) {
        String destination = user.getName() != null ? user.getName() : "(no name)";
        log.info("Sending Push Notification to {}: {}", destination, message);
    }

    @Override
    public String getChannelName() {
        return "Push Notification";
    }
}
