package com.RentFlow.enums;

public enum NotificationType {

    // =========================================================
    // PAYMENT
    // =========================================================

    PAYMENT_DUE,
    PAYMENT_OVERDUE,
    PAYMENT_RECEIVED,

    // =========================================================
    // MAINTENANCE
    // =========================================================

    MAINTENANCE_CREATED,
    MAINTENANCE_STARTED,
    MAINTENANCE_RESOLVED,
    MAINTENANCE_CLOSED,

    // =========================================================
    // LEASE
    // =========================================================

    LEASE_CREATED,
    LEASE_ACTIVATED,
    LEASE_TERMINATED,
    LEASE_EXPIRING
}