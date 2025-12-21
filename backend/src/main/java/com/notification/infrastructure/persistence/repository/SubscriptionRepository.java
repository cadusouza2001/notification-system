package com.notification.infrastructure.persistence.repository;


import com.notification.domain.model.Category;
import com.notification.domain.model.Subscription;
import com.notification.domain.model.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByCategory(Category category);
}
