package com.misha.tastyfast.repositories;

import com.misha.tastyfast.model.Notification;
import com.misha.tastyfast.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
