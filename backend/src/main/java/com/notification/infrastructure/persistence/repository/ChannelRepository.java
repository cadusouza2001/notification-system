package com.notification.infrastructure.persistence.repository;

import com.notification.domain.model.Channel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByName(String name);
}
