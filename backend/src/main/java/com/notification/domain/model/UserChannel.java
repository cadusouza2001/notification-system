package com.notification.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.*;

@Entity
@Table(name = "user_channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChannel {

    @EmbeddedId private UserChannelId id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_channels_user"))
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("channelId")
    @JoinColumn(
            name = "channel_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_channels_channel"))
    private Channel channel;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @NotNull
    @Column(name = "selected_at", nullable = false)
    private OffsetDateTime selectedAt;
}
