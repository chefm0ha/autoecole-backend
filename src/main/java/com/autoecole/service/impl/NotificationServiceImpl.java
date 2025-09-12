package com.autoecole.service.impl;

import com.autoecole.dto.response.NotificationDTO;
import com.autoecole.enums.NotificationStatus;
import com.autoecole.enums.NotificationType;
import com.autoecole.enums.UserRole;
import com.autoecole.model.Exam;
import com.autoecole.model.Notification;
import com.autoecole.model.User;
import com.autoecole.repository.NotificationDao;
import com.autoecole.repository.UserDao;
import com.autoecole.service.NotificationService;
import com.autoecole.service.WhatsAppService;
import com.autoecole.service.WebSocketNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;
    private final UserDao userDao;
    private final WhatsAppService whatsAppService;
    private final WebSocketNotificationService webSocketNotificationService;

    // ==================== CRUD OPERATIONS ====================

    @Override
    public List<NotificationDTO> getNotificationsByUser(User user) {
        log.debug("Getting all notifications for user: {}", user.getEmail());

        List<Notification> notifications;

        // Check if user is STAFF or ADMIN
        if (user.getRole() == UserRole.STAFF || user.getRole() == UserRole.ADMIN) {
            // Get both user-specific notifications AND broadcast EXAM_REMINDER notifications
            notifications = notificationDao.findNotificationsForStaffAndAdmin(user);
        } else {
            // Regular users only get their specific notifications
            notifications = notificationDao.findByUserOrderByCreatedAtDesc(user);
        }

        // Convert to DTOs
        return notifications.stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    @Override
    public long getUnreadNotificationCount(User user) {
        log.debug("Getting unread notification count for user: {}", user.getEmail());

        // Check if user is STAFF or ADMIN
        if (user.getRole() == UserRole.STAFF || user.getRole() == UserRole.ADMIN) {
            // Count both user-specific AND broadcast EXAM_REMINDER unread notifications
            return notificationDao.countUnreadNotificationsForStaffAndAdmin(user);
        } else {
            // Regular users only count their specific unread notifications
            return notificationDao.countByUserAndReadAtIsNull(user);
        }
    }

    // ==================== NOTIFICATION CREATION ====================

    @Override
    public Notification createExamReminderNotification(Exam exam) {
        log.debug("Creating exam reminder notification for exam {}", exam.getId());

        // For broadcast notifications (user = null), check if notification already exists for this exam
        if (broadcastNotificationExists(exam)) {
            log.debug("Broadcast exam reminder notification already exists for exam {}", exam.getId());
            return null;
        }

        // Build a notification message
        String message = buildExamReminderMessage(exam);
        String title = "Exam Reminder - 5 Days Left";

        // Create notification
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(NotificationType.EXAM_REMINDER)
                .status(NotificationStatus.PENDING)
                .exam(exam)
                .user(null) // null for broadcast notifications
                .whatsappSent(false)
                .build();

        // Save notification
        Notification savedNotification = notificationDao.save(notification);

        log.info("Created broadcast exam reminder notification {} for exam {}",
                    savedNotification.getId(), exam.getId());


        return savedNotification;
    }

    // ==================== NOTIFICATION SENDING ====================

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
                    webSocketNotificationService.sendNotificationCountUpdate(
                            notification.getUser().getEmail(),
                            unreadCount
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

        unreadNotifications.forEach(notification -> notification.setReadAt(now));

        notificationDao.saveAll(unreadNotifications);

        // Send updated notification count via WebSocket (should be 0 unread)
        webSocketNotificationService.sendNotificationCountUpdate(user.getEmail(), 0L);

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

    @Override
    public void sendBroadcastNotification(Notification notification) {
        log.debug("Sending broadcast notification: {}", notification.getId());

        try {
            // Get all STAFF and ADMIN users
            List<User> staffUsers = userDao.findByRole(UserRole.STAFF);
            List<User> adminUsers = userDao.findByRole(UserRole.ADMIN);

            List<User> allTargetUsers = new ArrayList<>();
            allTargetUsers.addAll(staffUsers);
            allTargetUsers.addAll(adminUsers);

            if (allTargetUsers.isEmpty()) {
                log.warn("No staff or admin users found to send broadcast notification to");
                return;
            }

            // Mark notification as sent
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationDao.save(notification);

            // Send to all target users via WebSocket
            for (User user : allTargetUsers) {
                try {
                    webSocketNotificationService.sendNotificationToUser(user.getEmail(), notification);

                    // Try to send WhatsApp if user has phone number
                    if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                        whatsAppService.sendMessage(user.getPhone(), notification.getMessage());
                    }
                } catch (Exception e) {
                    log.error("Failed to send broadcast notification to user {}: {}",
                            user.getEmail(), e.getMessage());
                }
            }

            log.info("Broadcast notification {} sent to {} users",
                    notification.getId(), allTargetUsers.size());

        } catch (Exception e) {
            log.error("Failed to send broadcast notification {}: {}",
                    notification.getId(), e.getMessage(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notificationDao.save(notification);
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private boolean broadcastNotificationExists(Exam exam) {
        // You'll need to add this query to NotificationDao
        return notificationDao.existsByExamAndTypeAndUserIsNull(exam, NotificationType.EXAM_REMINDER);
    }

    /**
     * Get unread notifications for a user
     */
    private List<Notification> getUnreadNotificationsByUser(User user) {
        // This method is referenced but not implemented in the original code
        // You may need to add this query to NotificationDao
        return notificationDao.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(notification -> notification.getReadAt() == null)
                .toList();
    }

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