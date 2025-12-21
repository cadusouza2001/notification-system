package com.notification.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String category;

    @NotBlank
    private String message;
}
