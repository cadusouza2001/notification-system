package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.User;
import org.example.infrastructure.persistence.entity.UserChannel;
import org.example.infrastructure.persistence.entity.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChannelRepository extends JpaRepository<UserChannel, UserChannelId> {
    List<UserChannel> findByUser(User user);
}
