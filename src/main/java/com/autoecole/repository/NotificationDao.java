package com.autoecole.repository;

import com.autoecole.enums.NotificationStatus;
import com.autoecole.enums.NotificationType;
import com.autoecole.model.Exam;
import com.autoecole.model.Notification;
import com.autoecole.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface NotificationDao extends CrudRepository<Notification, Long> {

    // Find notifications by user
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Find notifications that failed WhatsApp sending for retry
    @Query("SELECT n FROM Notification n WHERE n.whatsappSent = false AND n.status = :status AND n.recipientPhone IS NOT NULL")
    List<Notification> findFailedWhatsAppNotifications(@Param("status") NotificationStatus status);

    // Count unread notifications for a user
    long countByUserAndReadAtIsNull(User user);

    // Delete old notifications (cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :beforeDate")
    void deleteNotificationsOlderThan(@Param("beforeDate") LocalDateTime beforeDate);

    // Check if broadcast notification exists for exam and type
    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.exam = :exam AND n.type = :type AND n.user IS NULL")
    boolean existsByExamAndTypeAndUserIsNull(@Param("exam") Exam exam, @Param("type") NotificationType type);

    // Get notifications for STAFF/ADMIN: user-specific OR broadcast EXAM_REMINDER notifications
    @Query("SELECT n FROM Notification n WHERE " +
            "(n.user = :user) OR " +
            "(n.user IS NULL AND n.type = 'EXAM_REMINDER') " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsForStaffAndAdmin(@Param("user") User user);

    // Count unread notifications for STAFF/ADMIN: user-specific OR broadcast EXAM_REMINDER notifications
    @Query("SELECT COUNT(n) FROM Notification n WHERE " +
            "((n.user = :user) OR (n.user IS NULL AND n.type = 'EXAM_REMINDER')) " +
            "AND n.readAt IS NULL")
    long countUnreadNotificationsForStaffAndAdmin(@Param("user") User user);

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.exam = :exam AND n.user = :user")
    boolean existsByExamAndUser(@Param("exam") Exam exam, @Param("user") User user);
}