package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.UserChannel;
import org.example.infrastructure.persistence.entity.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChannelRepository extends JpaRepository<UserChannel, UserChannelId> {
}
