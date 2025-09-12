package com.autoecole.service;

import com.autoecole.model.Exam;
import com.autoecole.model.Notification;
import com.autoecole.model.User;

import java.util.List;

public interface NotificationService {

    // ==================== CRUD OPERATIONS ====================

    /**
     * Get all notifications for a specific user
     * @param user The user to get notifications for
     * @return List of notifications ordered by creation date (newest first)
     */
    List<Notification> getNotificationsByUser(User user);


    /**
     * Get count of unread notifications for a user
     * @param user The user to count notifications for
     * @return Number of unread notifications
     */
    long getUnreadNotificationCount(User user);


    // ==================== NOTIFICATION CREATION ====================

    /**
     * Create and save an exam reminder notification
     * @param exam The exam to create a reminder for
     * @param user The user to notify
     * @return Created notification or null if already exists
     */
    Notification createExamReminderNotification(Exam exam, User user);

    // ==================== NOTIFICATION SENDING ====================

    /**
     * Send in-app notification (via WebSocket)
     * @param notification The notification to send
     */
    void sendNotification(Notification notification);

    /**
     * Send WhatsApp notification
     * @param notification The notification to send via WhatsApp
     */
    void sendWhatsAppNotification(Notification notification);

    // ==================== NOTIFICATION MANAGEMENT ====================

    /**
     * Mark a specific notification as read
     * @param notificationId The notification ID to mark as read
     */
    void markAsRead(Long notificationId);

    /**
     * Mark all notifications as read for a user
     * @param user The user whose notifications to mark as read
     */
    void markAllAsRead(User user);


    // ==================== BULK OPERATIONS ====================

    /**
     * Retry failed WhatsApp notifications
     */
    void retryFailedNotifications();

    /**
     * Clean up old notifications (older than configured days)
     */
    void cleanupOldNotifications();
}