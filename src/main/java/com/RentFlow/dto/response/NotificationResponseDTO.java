package com.RentFlow.dto.response;

import java.time.LocalDateTime;

import com.RentFlow.enums.NotificationStatus;
import com.RentFlow.enums.NotificationType;

public class NotificationResponseDTO {

    private Long id;

    private NotificationType type;

    private String title;

    private String message;

    private NotificationStatus status;

    private LocalDateTime createdAt;

    public NotificationResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}