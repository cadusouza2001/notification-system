package org.example.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserChannelId implements Serializable {

    private Long userId;
    private Long channelId;
}
