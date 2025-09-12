package com.autoecole.service.impl;

import com.autoecole.enums.NotificationStatus;
import com.autoecole.enums.NotificationType;
import com.autoecole.model.Exam;
import com.autoecole.model.Notification;
import com.autoecole.model.User;
import com.autoecole.repository.NotificationDao;
import com.autoecole.service.NotificationService;
import com.autoecole.service.WhatsAppService;
import com.autoecole.service.WebSocketNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final WhatsAppService whatsAppService;
    private final WebSocketNotificationService webSocketNotificationService;

    // ==================== CRUD OPERATIONS ====================

    @Override
    public List<Notification> getNotificationsByUser(User user) {
        log.debug("Getting all notifications for user: {}", user.getEmail());
        return notificationDao.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long getUnreadNotificationCount(User user) {
        log.debug("Getting unread notification count for user: {}", user.getEmail());
        return notificationDao.countByUserAndReadAtIsNull(user);
    }

    @Override
    public Notification findById(Long id) {
        log.debug("Finding notification by ID: {}", id);
        return notificationDao.findById(id).orElse(null);
    }

    // ==================== NOTIFICATION CREATION ====================

    @Override
    public Notification createExamReminderNotification(Exam exam, User user) {
        log.debug("Creating exam reminder notification for exam {} and user {}", exam.getId(), user.getEmail());

        // Check if notification already exists to prevent duplicates
        if (notificationExists(exam, user, NotificationType.EXAM_REMINDER)) {
            log.debug("Exam reminder notification already exists for exam {} and user {}",
                    exam.getId(), user.getEmail());
            return null;
        }

        // Build a notification message
        String message = buildExamReminderMessage(exam);
        String title = "Exam Reminder - 5 Days Left";

        // Create notification
        Notification notification = Notification.createExamReminder(exam, user, message);
        notification.setTitle(title);
        notification.setRecipientPhone(user.getPhone()); // For WhatsApp

        // Save notification
        Notification savedNotification = notificationDao.save(notification);
        log.info("Created exam reminder notification {} for exam {} and user {}",
                savedNotification.getId(), exam.getId(), user.getEmail());

        return savedNotification;
    }

    @Override
    public Notification createNotification(User user, NotificationType type, String title, String message) {
        log.debug("Creating {} notification for user {}: {}", type, user.getEmail(), title);

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .status(NotificationStatus.PENDING)
                .recipientPhone(user.getPhone())
                .whatsappSent(false)
                .build();

        Notification savedNotification = notificationDao.save(notification);
        log.info("Created {} notification {} for user {}", type, savedNotification.getId(), user.getEmail());

        return savedNotification;
    }

    @Override
    public Notification createExamNotification(User user, Exam exam, NotificationType type, String title, String message) {
        log.debug("Creating exam {} notification for user {} and exam {}", type, user.getEmail(), exam.getId());

        Notification notification = Notification.builder()
                .user(user)
                .exam(exam)
                .type(type)
                .title(title)
                .message(message)
                .status(NotificationStatus.PENDING)
                .recipientPhone(user.getPhone())
                .whatsappSent(false)
                .build();

        Notification savedNotification = notificationDao.save(notification);
        log.info("Created exam {} notification {} for user {} and exam {}",
                type, savedNotification.getId(), user.getEmail(), exam.getId());

        return savedNotification;
    }

    // ==================== NOTIFICATION SENDING ====================

    @Override
    public void sendNotification(Notification notification) {
        try {
            log.debug("Sending in-app notification: {}", notification.getId());

            // Mark as sent
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationDao.save(notification);

            // Send via WebSocket
            if (notification.getUser() != null) {
                webSocketNotificationService.sendNotificationToUser(
                        notification.getUser().getEmail(),
                        notification
                );
            }

            log.info("In-app notification sent successfully: {}", notification.getId());

        } catch (Exception e) {
            log.error("Failed to send in-app notification {}: {}", notification.getId(), e.getMessage(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notificationDao.save(notification);
        }
    }

    @Override
    public void sendWhatsAppNotification(Notification notification) {
        if (notification.getRecipientPhone() == null || notification.getRecipientPhone().trim().isEmpty()) {
            log.warn("No phone number available for WhatsApp notification: {}", notification.getId());
            return;
        }

        try {
            log.debug("Sending WhatsApp notification: {}", notification.getId());

            boolean sent = whatsAppService.sendMessage(
                    notification.getRecipientPhone(),
                    notification.getMessage()
            );

            if (sent) {
                notification.setWhatsappSent(true);
                notification.setWhatsappSentAt(LocalDateTime.now());
                log.info("WhatsApp notification sent successfully: {}", notification.getId());
            } else {
                log.error("Failed to send WhatsApp notification: {}", notification.getId());
                notification.setErrorMessage("WhatsApp sending failed");
            }

            notificationDao.save(notification);

        } catch (Exception e) {
            log.error("Error sending WhatsApp notification {}: {}", notification.getId(), e.getMessage(), e);
            notification.setErrorMessage("WhatsApp error: " + e.getMessage());
            notificationDao.save(notification);
        }
    }

    @Override
    public void sendNotificationAllChannels(Notification notification) {
        log.debug("Sending notification {} via all channels", notification.getId());

        // Send in-app notification
        sendNotification(notification);

        // Send WhatsApp notification
        sendWhatsAppNotification(notification);

        log.info("Notification {} sent via all available channels", notification.getId());
    }

    // ==================== NOTIFICATION MANAGEMENT ====================

    @Override
    public void markAsRead(Long notificationId) {
        log.debug("Marking notification {} as read", notificationId);

        Optional<Notification> optionalNotification = notificationDao.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();

            if (notification.getReadAt() == null) {
                notification.setReadAt(LocalDateTime.now());
                notificationDao.save(notification);

                // Send updated notification count via WebSocket
                if (notification.getUser() != null) {
                    long unreadCount = getUnreadNotificationCount(notification.getUser());
                    long totalCount = getNotificationsByUser(notification.getUser()).size();
                    webSocketNotificationService.sendNotificationCountUpdate(
                            notification.getUser().getEmail(),
                            unreadCount,
                            totalCount
                    );
                }

                log.info("Marked notification {} as read", notificationId);
            }
        } else {
            log.warn("Notification {} not found for marking as read", notificationId);
        }
    }

    @Override
    public void markAllAsRead(User user) {
        log.debug("Marking all notifications as read for user: {}", user.getEmail());

        List<Notification> unreadNotifications = getUnreadNotificationsByUser(user);
        LocalDateTime now = LocalDateTime.now();

        unreadNotifications.forEach(notification -> {
            notification.setReadAt(now);
        });

        notificationDao.saveAll(unreadNotifications);

        // Send updated notification count via WebSocket (should be 0 unread)
        long totalCount = getNotificationsByUser(user).size();
        webSocketNotificationService.sendNotificationCountUpdate(user.getEmail(), 0L, totalCount);

        log.info("Marked {} notifications as read for user {}", unreadNotifications.size(), user.getEmail());
    }

    // ==================== BULK OPERATIONS ====================

    @Override
    public void retryFailedNotifications() {
        log.debug("Retrying failed WhatsApp notifications");

        List<Notification> failedNotifications =
                notificationDao.findFailedWhatsAppNotifications(NotificationStatus.SENT);

        for (Notification notification : failedNotifications) {
            log.info("Retrying WhatsApp notification: {}", notification.getId());
            sendWhatsAppNotification(notification);
        }

        log.info("Retried {} failed WhatsApp notifications", failedNotifications.size());
    }

    @Override
    public void cleanupOldNotifications() {
        log.debug("Cleaning up old notifications");

        // Delete notifications older than 30 days
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        notificationDao.deleteNotificationsOlderThan(cutoffDate);

        log.info("Cleaned up notifications older than {}", cutoffDate);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Build an exam reminder message with formatted content
     */
    private String buildExamReminderMessage(Exam exam) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String candidateName = "Unknown";
        String candidateCin = "Unknown";
        String categoryCode = "Unknown";

        // Safely extract candidate information
        if (exam.getApplicationFile() != null && exam.getApplicationFile().getCandidate() != null) {
            var candidate = exam.getApplicationFile().getCandidate();
            candidateName = candidate.getFirstName() + " " + candidate.getLastName();
            candidateCin = candidate.getCin();
        }

        // Safely extract category information
        if (exam.getApplicationFile() != null && exam.getApplicationFile().getCategory() != null) {
            categoryCode = exam.getApplicationFile().getCategory().getCode();
        }

        return String.format(
                """
                    🚨 EXAM REMINDER - 5 Days Left!
                    📅 Date: %s
                    📋 Type: %s
                    👤 Candidate: %s
                    🆔 CIN: %s
                    📚 Category: %s
                    Please ensure all preparations are complete for this upcoming exam.
                """,
                exam.getDate().format(formatter),
                exam.getExamType().name(),
                candidateName,
                candidateCin,
                categoryCode
        );
    }
}