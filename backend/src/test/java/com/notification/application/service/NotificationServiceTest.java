package com.notification.application.service;

import com.notification.domain.model.Category;
import com.notification.domain.model.Channel;
import com.notification.domain.model.NotificationLog;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.*;

class NotificationServiceTest {

    @Mock CategoryRepository categoryRepository;
    @Mock SubscriptionRepository subscriptionRepository;
    @Mock UserChannelRepository userChannelRepository;
    @Mock NotificationLogRepository notificationLogRepository;

    @Mock NotificationStrategy smsStrategy;
    @Mock NotificationStrategy emailStrategy;
    @Mock NotificationStrategy pushStrategy;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(smsStrategy.getChannelName()).willReturn("SMS");
        given(emailStrategy.getChannelName()).willReturn("E-Mail");
        given(pushStrategy.getChannelName()).willReturn("Push Notification");
        notificationService = new NotificationService(
                Set.of(smsStrategy, emailStrategy, pushStrategy),
                categoryRepository,
                subscriptionRepository,
                userChannelRepository,
                notificationLogRepository
        );
    }

    @Test
    void givenSportsNotificationRequest_whenSendingViaSms_thenCallsSmsStrategyOnly() {
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
    void givenSmsStrategyThrowsException_whenSendingNotification_thenServiceDoesNotPropagateAndNoLogSaved() {
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

    @Test
    void givenInvalidCategoryName_whenSendingNotification_thenThrowsIllegalArgumentExceptionAndNoLogSaved() {
        // Given: invalid category name
        NotificationRequest req = new NotificationRequest();
        req.setCategory("InvalidCategory");
        req.setMessage("Test message");

        given(categoryRepository.findByName("InvalidCategory")).willReturn(Optional.empty());

        // When & Then
        try {
            notificationService.sendNotification(req);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected exception
        }

        then(notificationLogRepository).should(never()).save(any());
        then(smsStrategy).should(never()).send(any(), any());
        then(emailStrategy).should(never()).send(any(), any());
        then(pushStrategy).should(never()).send(any(), any());
    }

    @Test
    void givenValidCategoryButNoSubscriptions_whenSendingNotification_thenNoNotificationSentAndNoLogSaved() {
        // Given: valid category but no subscriptions
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Test message");

        Category sports = new Category();
        sports.setName("Sports");

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of()); // empty list

        // When
        notificationService.sendNotification(req);

        // Then
        then(smsStrategy).should(never()).send(any(), any());
        then(emailStrategy).should(never()).send(any(), any());
        then(pushStrategy).should(never()).send(any(), any());
        then(notificationLogRepository).should(never()).save(any());
    }

    @Test
    void givenUserWithNoChannels_whenSendingNotification_thenNoNotificationSentAndNoLogSaved() {
        // Given: valid subscription but user has no channels
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Test message");

        Category sports = new Category();
        sports.setName("Sports");

        User user = new User();
        user.setId(1L);
        user.setEmail("u1@example.com");

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setCategory(sports);

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of(sub));
        given(userChannelRepository.findByUser(user)).willReturn(List.of()); // empty list

        // When
        notificationService.sendNotification(req);

        // Then
        then(smsStrategy).should(never()).send(any(), any());
        then(emailStrategy).should(never()).send(any(), any());
        then(pushStrategy).should(never()).send(any(), any());
        then(notificationLogRepository).should(never()).save(any());
    }

    @Test
    void givenUserWithDisabledChannel_whenSendingNotification_thenNoNotificationSentAndNoLogSaved() {
        // Given: user has channel but it's disabled
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Test message");

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
        ucSms.setEnabled(false); // disabled

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of(sub));
        given(userChannelRepository.findByUser(user)).willReturn(List.of(ucSms));

        // When
        notificationService.sendNotification(req);

        // Then
        then(smsStrategy).should(never()).send(any(), any());
        then(emailStrategy).should(never()).send(any(), any());
        then(pushStrategy).should(never()).send(any(), any());
        then(notificationLogRepository).should(never()).save(any());
    }

    @Test
    void givenChannelWithoutStrategy_whenSendingNotification_thenSkipsChannelAndNoLogSaved() {
        // Given: user has channel but no strategy registered for it
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Test message");

        Category sports = new Category();
        sports.setName("Sports");

        User user = new User();
        user.setId(1L);
        user.setEmail("u1@example.com");

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setCategory(sports);

        Channel unknownChannel = new Channel();
        unknownChannel.setName("UnknownChannel"); // no strategy for this

        UserChannel uc = new UserChannel();
        uc.setUser(user);
        uc.setChannel(unknownChannel);
        uc.setEnabled(true);

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of(sub));
        given(userChannelRepository.findByUser(user)).willReturn(List.of(uc));

        // When
        notificationService.sendNotification(req);

        // Then
        then(smsStrategy).should(never()).send(any(), any());
        then(emailStrategy).should(never()).send(any(), any());
        then(pushStrategy).should(never()).send(any(), any());
        then(notificationLogRepository).should(never()).save(any());
    }

    @Test
    void givenUserWithAllChannelsEnabled_whenSendingNotification_thenSendsToAllChannels() {
        // Given: user has all three channels enabled
        NotificationRequest req = new NotificationRequest();
        req.setCategory("Sports");
        req.setMessage("Multi-channel message");

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
        Channel email = new Channel();
        email.setName("E-Mail");
        Channel push = new Channel();
        push.setName("Push Notification");

        UserChannel ucSms = new UserChannel();
        ucSms.setUser(user);
        ucSms.setChannel(sms);
        ucSms.setEnabled(true);

        UserChannel ucEmail = new UserChannel();
        ucEmail.setUser(user);
        ucEmail.setChannel(email);
        ucEmail.setEnabled(true);

        UserChannel ucPush = new UserChannel();
        ucPush.setUser(user);
        ucPush.setChannel(push);
        ucPush.setEnabled(true);

        given(categoryRepository.findByName("Sports")).willReturn(Optional.of(sports));
        given(subscriptionRepository.findByCategory(sports)).willReturn(List.of(sub));
        given(userChannelRepository.findByUser(user)).willReturn(List.of(ucSms, ucEmail, ucPush));

        // When
        notificationService.sendNotification(req);

        // Then - all strategies called
        then(smsStrategy).should().send(eq(user), eq("Multi-channel message"));
        then(emailStrategy).should().send(eq(user), eq("Multi-channel message"));
        then(pushStrategy).should().send(eq(user), eq("Multi-channel message"));
        then(notificationLogRepository).should(times(3)).save(any());
    }

    @Test
    void givenLogsInRepository_whenGettingAllLogs_thenReturnsFormattedLogs() {
        // Given: mock logs in repository
        NotificationLog log1 = NotificationLog.builder()
                .id(1L)
                .message("Test message 1")
                .createdAt(java.time.OffsetDateTime.now())
                .build();

        User user = new User();
        user.setName("John Doe");
        log1.setUser(user);

        Category category = new Category();
        category.setName("Sports");
        log1.setCategory(category);

        Channel channel = new Channel();
        channel.setName("SMS");
        log1.setChannel(channel);

        given(notificationLogRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .willReturn(List.of(log1));

        // When
        var result = notificationService.getAllLogs();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getUserName()).isEqualTo("John Doe");
        assertThat(result.getFirst().getCategory()).isEqualTo("Sports");
        assertThat(result.getFirst().getChannel()).isEqualTo("SMS");
        assertThat(result.getFirst().getMessage()).isEqualTo("Test message 1");
        assertThat(result.getFirst().getType()).isEqualTo("Notification");
    }

    @Test
    void givenLogWithNullFields_whenGettingAllLogs_thenHandlesNullFields() {
        // Given: log with null user, category, channel
        NotificationLog log = NotificationLog.builder()
                .id(2L)
                .message("Test message")
                .createdAt(null)
                .user(null)
                .category(null)
                .channel(null)
                .build();

        given(notificationLogRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .willReturn(List.of(log));

        // When
        var result = notificationService.getAllLogs();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUserName()).isNull();
        assertThat(result.getFirst().getCategory()).isNull();
        assertThat(result.getFirst().getChannel()).isNull();
        assertThat(result.getFirst().getTimestamp()).isNull();
    }
}
