package org.example.application.service;

import org.example.application.dto.LogResponse;
import org.example.application.dto.NotificationRequest;
import org.example.domain.strategy.NotificationStrategy;
import org.example.infrastructure.persistence.entity.*;
import org.example.infrastructure.persistence.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final CategoryRepository categoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserChannelRepository userChannelRepository;
    private final NotificationLogRepository notificationLogRepository;

    private final Map<String, NotificationStrategy> strategiesByName = new HashMap<>();

    public NotificationService(
            Set<NotificationStrategy> strategies,
            CategoryRepository categoryRepository,
            SubscriptionRepository subscriptionRepository,
            UserChannelRepository userChannelRepository,
            NotificationLogRepository notificationLogRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userChannelRepository = userChannelRepository;
        this.notificationLogRepository = notificationLogRepository;

        for (NotificationStrategy s : strategies) {
            strategiesByName.put(s.getChannelName(), s);
        }
    }

    public void sendNotification(NotificationRequest request) {
        Category category = categoryRepository.findByName(request.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategory()));

        List<Subscription> subsForCategory = subscriptionRepository.findByCategory(category);
        log.info("Dispatching notifications for category '{}' to {} subscribed users",
                category.getName(), subsForCategory.size());

        for (Subscription sub : subsForCategory) {
            User user = sub.getUser();
            if (user == null) {
                continue;
            }

            List<UserChannel> userChannels = userChannelRepository.findByUser(user);

            for (UserChannel uc : userChannels) {
                Channel channel = uc.getChannel();
                if (channel == null || !Boolean.TRUE.equals(uc.getEnabled())) {
                    continue;
                }
                String channelName = channel.getName();
                NotificationStrategy strategy = strategiesByName.get(channelName);
                if (strategy == null) {
                    log.warn("No strategy registered for channel '{}'. Skipping.", channelName);
                    continue;
                }

                try {
                    strategy.send(user, request.getMessage());

                    NotificationLog logEntry = NotificationLog.builder()
                            .user(user)
                            .category(category)
                            .channel(channel)
                            .message(request.getMessage())
                            .createdAt(OffsetDateTime.now())
                            .build();

                    notificationLogRepository.save(logEntry);
                } catch (Exception ex) {
                    log.error("Failed to send via channel '{}' for user '{}': {}",
                            channelName, user.getEmail(), ex.getMessage(), ex);
                }
            }
        }
    }

    public List<LogResponse> getAllLogs() {
        List<NotificationLog> logs = notificationLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        List<LogResponse> responses = new ArrayList<>(logs.size());
        for (NotificationLog l : logs) {
            responses.add(LogResponse.builder()
                    .id(l.getId())
                    .type("Notification")
                    .userName(l.getUser() != null ? l.getUser().getName() : null)
                    .category(l.getCategory() != null ? l.getCategory().getName() : null)
                    .channel(l.getChannel() != null ? l.getChannel().getName() : null)
                    .message(l.getMessage())
                    .timestamp(l.getCreatedAt() != null ? l.getCreatedAt().format(fmt) : null)
                    .build());
        }
        return responses;
    }
}
