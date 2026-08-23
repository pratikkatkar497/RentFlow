package com.RentFlow.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RentFlow.dto.response.NotificationResponseDTO;
import com.RentFlow.entity.Notification;
import com.RentFlow.entity.User;
import com.RentFlow.enums.NotificationStatus;
import com.RentFlow.enums.NotificationType;
import com.RentFlow.exception.AccessDeniedException;
import com.RentFlow.exception.ResourceNotFoundException;
import com.RentFlow.repository.NotificationRepository;
import com.RentFlow.repository.UserRepository;
import com.RentFlow.service.NotificationService;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;
    }

    // =========================================================
    // Get Current User
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated");
        }

        String email =
                authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
    }

    // =========================================================
    // Get My Notifications
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO>
            getMyNotifications() {

        User currentUser =
                getCurrentUser();

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(
                        currentUser)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Get My Unread Notifications
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO>
            getMyUnreadNotifications() {

        User currentUser =
                getCurrentUser();

        return notificationRepository
                .findByUserAndStatusOrderByCreatedAtDesc(
                        currentUser,
                        NotificationStatus.UNREAD)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // Get Unread Count
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        User currentUser =
                getCurrentUser();

        return notificationRepository
                .countByUserAndStatus(
                        currentUser,
                        NotificationStatus.UNREAD);
    }

    // =========================================================
    // Mark One As Read
    // =========================================================

    @Override
    public void markAsRead(
            Long notificationId) {

        User currentUser =
                getCurrentUser();

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"));

        /*
         * Security:
         * User can only modify own notification.
         */
        if (!notification.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to update this notification");
        }

        if (notification.getStatus() ==
                NotificationStatus.READ) {

            return;
        }

        notification.setStatus(
                NotificationStatus.READ);

        notificationRepository.save(
                notification);
    }

    // =========================================================
    // Mark All As Read
    // =========================================================

    @Override
    public void markAllAsRead() {

        User currentUser =
                getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndStatusOrderByCreatedAtDesc(
                                currentUser,
                                NotificationStatus.UNREAD);

        if (notifications.isEmpty()) {
            return;
        }

        for (Notification notification :
                notifications) {

            notification.setStatus(
                    NotificationStatus.READ);
        }

        notificationRepository.saveAll(
                notifications);
    }

    // =========================================================
    // Create Notification
    // =========================================================

    @Override
    public void createNotification(
            Long userId,
            NotificationType type,
            String title,
            String message) {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID is required");
        }

        if (type == null) {

            throw new IllegalArgumentException(
                    "Notification type is required");
        }

        if (title == null ||
                title.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Notification title is required");
        }

        if (message == null ||
                message.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Notification message is required");
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"));

        Notification notification =
                new Notification();

        notification.setUser(user);

        notification.setType(type);

        notification.setTitle(title);

        notification.setMessage(message);

        notification.setStatus(
                NotificationStatus.UNREAD);

        notification.setCreatedAt(
                LocalDateTime.now());

        notificationRepository.save(
                notification);
    }

    // =========================================================
    // Entity → DTO
    // =========================================================

    private NotificationResponseDTO
            convertToResponse(
                    Notification notification) {

        NotificationResponseDTO response =
                new NotificationResponseDTO();

        response.setId(
                notification.getId());

        response.setType(
                notification.getType());

        response.setTitle(
                notification.getTitle());

        response.setMessage(
                notification.getMessage());

        response.setStatus(
                notification.getStatus());

        response.setCreatedAt(
                notification.getCreatedAt());

        return response;
    }
}