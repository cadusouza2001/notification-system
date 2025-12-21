package com.notification.infrastructure;

import com.notification.NotificationBackendApplication;
import com.notification.infrastructure.persistence.repository.*;
import com.notification.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NotificationBackendApplication.class, properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
class NotificationIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private UserChannelRepository userChannelRepository;
    @Autowired private NotificationLogRepository logRepository;

    private Long savedUserId;

    @BeforeEach
    void seedData() {
        // Clear
        logRepository.deleteAll();
        userChannelRepository.deleteAll();
        subscriptionRepository.deleteAll();
        channelRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Seed minimal graph: user subscribes to Sports, has SMS channel enabled
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user = userRepository.save(user);
        savedUserId = user.getId();

        Category sports = new Category();
        sports.setName("Sports");
        sports = categoryRepository.save(sports);

        Channel sms = new Channel();
        sms.setName("SMS");
        sms = channelRepository.save(sms);

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setCategory(sports);
        sub.setSubscribedAt(java.time.OffsetDateTime.now());
        SubscriptionId subId = new SubscriptionId();
        subId.setUserId(user.getId());
        subId.setCategoryId(sports.getId());
        sub.setId(subId);
        subscriptionRepository.save(sub);

        UserChannel uc = new UserChannel();
        uc.setUser(user);
        uc.setChannel(sms);
        uc.setEnabled(true);
        uc.setSelectedAt(java.time.OffsetDateTime.now());
        UserChannelId ucId = new UserChannelId();
        ucId.setUserId(user.getId());
        ucId.setChannelId(sms.getId());
        uc.setId(ucId);
        userChannelRepository.save(uc);
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void fullFlow_success_persistsLog() throws Exception {
        // Given: build JSON using the actual saved ID
        String payload = String.format("""
        {
          "userId": %d,
          "category": "Sports",
          "channel": "SMS",
          "message": "Integration test message"
        }
        """, savedUserId);

        // When
        mockMvc.perform(
                post("/api/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload)
            )
            // Then - HTTP (controller returns 202 ACCEPTED)
            .andExpect(status().isAccepted());

        // Then - DB
        long count = logRepository.count();
        assertThat(count).isEqualTo(1L);
        var saved = logRepository.findAll().get(0);
        assertThat(saved.getMessage()).isEqualTo("Integration test message");
        assertThat(saved.getCategory().getName()).isEqualTo("Sports");
        assertThat(saved.getChannel().getName()).isEqualTo("SMS");
        assertThat(saved.getUser()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(savedUserId);
    }
}
