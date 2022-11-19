package com.ttn.ecommerce.repository;

import com.ttn.ecommerce.entity.NotificationToken;
import com.ttn.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {
    NotificationToken findByToken(String token);

    NotificationToken findByUser(User user);
}