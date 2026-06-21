package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.NotificationResponse;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.entity.Notification;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.repository.NotificationRepository;
import com.carwash.carwashsystem.service.interfaces.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<Notification> getNotificationsForCustomer(Long customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long customerId) {
        return notificationRepository.findByCustomerIdAndIsReadFalse(customerId);
    }

    @Transactional
    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    @Override
    public void markAllAsRead(Long customerId) {
        notificationRepository.markAllAsRead(customerId);
    }

    @Transactional
    @Override
    public void sendNotification(Long customerId, String title, String message) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Notification notification = Notification.builder()
                .customer(customer)   // gán object Customer
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationResponse> getNotificationsByCustomer(Long customerId, Pageable pageable) {
        return null;
    }
}
