package com.notification.presentation.controller;

import com.notification.application.service.NotificationService;
import com.notification.presentation.dto.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @Test
    void givenRequestWithMissingUserId_whenSendingNotification_thenReturns400() throws Exception {
        // Given: request without userId
        NotificationRequest request = NotificationRequest.builder()
                .category("Sports")
                .message("Test message")
                // userId is null
                .build();

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        then(notificationService).should(never()).sendNotification(any());
    }

    @Test
    void givenRequestWithEmptyMessage_whenSendingNotification_thenReturns400() throws Exception {
        // Given: request with empty message
        NotificationRequest request = NotificationRequest.builder()
                .userId(1L)
                .category("Sports")
                .message("")  // empty message
                .build();

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        then(notificationService).should(never()).sendNotification(any());
    }

    @Test
    void givenRequestWithNullCategory_whenSendingNotification_thenReturns400() throws Exception {
        // Given: request with null category
        NotificationRequest request = NotificationRequest.builder()
                .userId(1L)
                .category(null)  // null category
                .message("Test message")
                .build();

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        then(notificationService).should(never()).sendNotification(any());
    }

    @Test
    void givenServiceThrowsIllegalArgumentException_whenSendingNotification_thenReturns400() throws Exception {
        // Given: valid request but service throws IllegalArgumentException
        NotificationRequest request = NotificationRequest.builder()
                .userId(1L)
                .category("InvalidCategory")
                .message("Test message")
                .build();

        willThrow(new IllegalArgumentException("Category not found: InvalidCategory"))
                .given(notificationService).sendNotification(any());

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Category not found: InvalidCategory"));
    }

    @Test
    void givenServiceThrowsRuntimeException_whenSendingNotification_thenReturns500() throws Exception {
        // Given: valid request but service throws RuntimeException
        NotificationRequest request = NotificationRequest.builder()
                .userId(1L)
                .category("Sports")
                .message("Test message")
                .build();

        willThrow(new RuntimeException("Database connection failed"))
                .given(notificationService).sendNotification(any());

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal error: Database connection failed"));
    }

    @Test
    void givenValidRequest_whenSendingNotification_thenReturns202() throws Exception {
        // Given: valid request
        NotificationRequest request = NotificationRequest.builder()
                .userId(1L)
                .category("Sports")
                .message("Game tonight!")
                .build();

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        then(notificationService).should().sendNotification(any());
    }

    @Test
    void givenServiceReturnsLogs_whenGettingHistory_thenReturnsLogs() throws Exception {
        // Given: service returns logs
        given(notificationService.getAllLogs()).willReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/notifications/log"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        then(notificationService).should().getAllLogs();
    }
}
