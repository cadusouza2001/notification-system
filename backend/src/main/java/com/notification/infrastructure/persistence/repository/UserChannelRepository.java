package com.notification.infrastructure.persistence.repository;

import com.notification.domain.model.User;
import com.notification.domain.model.UserChannel;
import com.notification.domain.model.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChannelRepository extends JpaRepository<UserChannel, UserChannelId> {
    List<UserChannel> findByUser(User user);
}
