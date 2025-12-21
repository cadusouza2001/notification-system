package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
