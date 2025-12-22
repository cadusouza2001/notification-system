package com.notification.infrastructure.persistence.repository;

import com.notification.domain.model.Category;
import com.notification.domain.model.Subscription;
import com.notification.domain.model.SubscriptionId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByCategory(Category category);
}
