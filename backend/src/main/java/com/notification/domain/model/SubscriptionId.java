package com.notification.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubscriptionId implements Serializable {

    private Long userId;
    private Long categoryId;
}
