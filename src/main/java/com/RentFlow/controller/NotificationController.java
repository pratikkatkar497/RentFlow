package com.RentFlow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RentFlow.constant.AppConstants;
import com.RentFlow.dto.response.NotificationResponseDTO;
import com.RentFlow.response.ApiResponse;
import com.RentFlow.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    // =========================================================
    // Get My Notifications
    // =========================================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<NotificationResponseDTO>>>
            getMyNotifications() {

        List<NotificationResponseDTO> response =
                notificationService
                        .getMyNotifications();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Get Unread Notifications
    // =========================================================

    @GetMapping("/unread")
    public ResponseEntity<
            ApiResponse<List<NotificationResponseDTO>>>
            getMyUnreadNotifications() {

        List<NotificationResponseDTO> response =
                notificationService
                        .getMyUnreadNotifications();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        response));
    }

    // =========================================================
    // Get Unread Count
    // =========================================================

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>>
            getUnreadCount() {

        long count =
                notificationService
                        .getUnreadCount();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        AppConstants.SUCCESS,
                        count));
    }

    // =========================================================
    // Mark One As Read
    // =========================================================

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>>
            markAsRead(
                    @PathVariable Long notificationId) {

        notificationService
                .markAsRead(notificationId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification marked as read successfully.",
                        null));
    }

    // =========================================================
    // Mark All As Read
    // =========================================================

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<String>>
            markAllAsRead() {

        notificationService
                .markAllAsRead();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All notifications marked as read successfully.",
                        null));
    }
}