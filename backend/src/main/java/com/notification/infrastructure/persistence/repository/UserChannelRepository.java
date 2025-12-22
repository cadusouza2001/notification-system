package com.notification.infrastructure.persistence.repository;

import com.notification.domain.model.User;
import com.notification.domain.model.UserChannel;
import com.notification.domain.model.UserChannelId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChannelRepository extends JpaRepository<UserChannel, UserChannelId> {
    List<UserChannel> findByUser(User user);
}
