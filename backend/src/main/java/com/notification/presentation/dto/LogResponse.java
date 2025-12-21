package com.notification.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogResponse {
    private Long id;
    private String type;        // e.g., "Notification"
    private String userName;
    private String category;
    private String channel;
    private String message;
    private String timestamp;   // ISO-8601 string for readability
}
