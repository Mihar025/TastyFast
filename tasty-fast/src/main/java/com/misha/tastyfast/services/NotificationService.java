package com.misha.tastyfast.services;

import com.misha.tastyfast.model.Notification;
import com.misha.tastyfast.model.User;
import com.misha.tastyfast.repositories.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.NotIdentifiableEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User user, String message){
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

    }

        public List<Notification> getUnreadNotifications(User user){
            return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                    .stream()
                    .filter(r -> !r.isRead())
                    .collect(Collectors.toList());
        }

        public void markAsRead(Integer notificationId){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
        }



}
