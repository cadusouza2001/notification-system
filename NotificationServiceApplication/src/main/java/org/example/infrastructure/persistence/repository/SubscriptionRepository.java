package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.Subscription;
import org.example.infrastructure.persistence.entity.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
}
