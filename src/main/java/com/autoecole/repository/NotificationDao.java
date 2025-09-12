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
}