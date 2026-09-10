package com.RentFlow.service;

import java.util.List;

import com.RentFlow.dto.response.NotificationResponseDTO;
import com.RentFlow.enums.NotificationType;

public interface NotificationService {

    List<NotificationResponseDTO>
    getMyNotifications();

    List<NotificationResponseDTO>
    getMyUnreadNotifications();

    long getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    boolean notificationExists(
            Long userId,
            NotificationType type,
            String message);
    
    void createNotification(
            Long userId,
            NotificationType type,
            String title,
            String message);
}