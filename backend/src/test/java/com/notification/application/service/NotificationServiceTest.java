package com.notification.application.service;

import com.notification.domain.model.Category;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Subscription;
import com.notification.domain.model.User;
import com.notification.domain.model.UserChannel;
import com.notification.domain.strategy.NotificationStrategy;
import com.notification.infrastructure.persistence.repository.CategoryRepository;
import com.notification.infrastructure.persistence.repository.NotificationLogRepository;
import com.notification.infrastructure.persistence.repository.SubscriptionRepository;
import com.notification.infrastructure.persistence.repository.UserChannelRepository;
import com.notification.presentation.dto.NotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.*;

class NotificationServiceTest {

    @Mock CategoryRepository categoryRepository;
    @Mock SubscriptionRepository subscriptionRepository;
    @Mock UserChannelRepository userChannelRepository;
    @Mock NotificationLogRepository notificationLogRepository;

    @Mock NotificationStrategy smsStrategy;
    @Mock NotificationStrategy emailStrategy;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(smsStrategy.getChannelName()).willReturn("SMS");
        given(emailStrategy.getChannelName()).willReturn("E-Mail");
        notificationService = new NotificationService(
                Set.of(smsStrategy, emailStrategy),
                categoryRepository,
                subscriptionRepository,
                userChannelRepository,
                notificationLogRepository
        );
    }

    @Test
    void strategyRouting_sportsViaSms_callsSmsOnly() {
        // Given
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Game tonight!");

        Category sports = new Category();
        sports.setName("Sports");

        User user = new User();
        user.setId(1L);
        user.setEmail("u1@example.com");

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setCategory(sports);

        Channel sms = new Channel();
        sms.setName("SMS");

        UserChannel ucSms = new UserChannel();
        ucSms.setUser(user);
        ucSms.setChannel(sms);
        ucSms.setEnabled(true);

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of(sub));
        given(userChannelRepository.findByUser(user)).willReturn(List.of(ucSms));

        // When
        notificationService.sendNotification(req);

        // Then
        then(smsStrategy).should().send(eq(user), eq("Game tonight!"));
        then(emailStrategy).should(never()).send(any(), any());
    }

    @Test
    void faultTolerance_smsThrows_serviceDoesNotPropagate_andNoLogSave() {
        // Given
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Match update");

        Category sports = new Category();
        sports.setName("Sports");

        User user = new User();
        user.setId(2L);
        user.setEmail("u2@example.com");

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setCategory(sports);

        Channel sms = new Channel();
        sms.setName("SMS");

        UserChannel ucSms = new UserChannel();
        ucSms.setUser(user);
        ucSms.setChannel(sms);
        ucSms.setEnabled(true);

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of(sub));
        given(userChannelRepository.findByUser(user)).willReturn(List.of(ucSms));

        willThrow(new RuntimeException("SMS failure"))
                .given(smsStrategy).send(eq(user), eq("Match update"));

        // When
        notificationService.sendNotification(req);

        // Then (fault tolerance): no exception propagated, and no log persisted on failure
        then(notificationLogRepository).should(never()).save(any());
    }
}
