package org.example.domain.strategy;

import org.example.infrastructure.persistence.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailStrategy implements NotificationStrategy {
    private static final Logger log = LoggerFactory.getLogger(EmailStrategy.class);

    @Override
    public void send(User user, String message) {
        String destination = user.getEmail() != null ? user.getEmail() : "(no email)";
        log.info("Sending E-Mail to {}: {}", destination, message);
    }

    @Override
    public String getChannelName() {
        return "E-Mail";
    }
}
