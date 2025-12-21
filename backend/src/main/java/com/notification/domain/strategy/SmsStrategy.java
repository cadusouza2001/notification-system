package com.notification.domain.strategy;

import com.notification.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsStrategy implements NotificationStrategy {
    private static final Logger log = LoggerFactory.getLogger(SmsStrategy.class);

    @Override
    public void send(User user, String message) {
        String destination = user.getPhoneNumber() != null ? user.getPhoneNumber() : "(no phone)";
        log.info("Sending SMS to {}: {}", destination, message);
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}
