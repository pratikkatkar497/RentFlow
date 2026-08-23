package com.RentFlow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RentFlow.entity.Notification;
import com.RentFlow.entity.User;
import com.RentFlow.enums.NotificationStatus;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(
            User user);

    List<Notification>
    findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            NotificationStatus status);

    long countByUserAndStatus(
            User user,
            NotificationStatus status);
}