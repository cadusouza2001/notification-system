package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.Category;
import org.example.infrastructure.persistence.entity.Subscription;
import org.example.infrastructure.persistence.entity.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByCategory(Category category);
}
