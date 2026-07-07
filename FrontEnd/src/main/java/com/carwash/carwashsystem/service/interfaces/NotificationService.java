package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.response.NotificationResponse;
import com.carwash.carwashsystem.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    @Transactional
    void markAllAsRead(Long customerId);

    void sendNotification(Long customerId, String title, String content);
    Page<NotificationResponse> getNotificationsByCustomer(Long customerId, Pageable pageable);

    List<Notification> getNotificationsForCustomer(Long customerId);

    List<Notification> getUnreadNotifications(Long customerId);

    void markAsRead(Long notificationId);
}