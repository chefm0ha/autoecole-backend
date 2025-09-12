package com.autoecole.service;

import com.autoecole.dto.response.websocket.NotificationMessage;
import com.autoecole.dto.common.WebSocketResponse;
import com.autoecole.model.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to a specific user
     */
    public void sendNotificationToUser(String userEmail, Notification notification) {
        try {
            NotificationMessage notificationMessage = NotificationMessage.fromNotification(notification);
            WebSocketResponse<NotificationMessage> response = WebSocketResponse.notification(notificationMessage);

            // Send it to user's personal queue
            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    response
            );

            log.debug("Sent WebSocket notification to user: {}", userEmail);

        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user {}: {}", userEmail, e.getMessage(), e);
        }
    }

    /**
     * Send notification count update to user
     */
    public void sendNotificationCountUpdate(String userEmail, long unreadCount) {
        try {
            WebSocketResponse<Long> response = WebSocketResponse.update(unreadCount, "Notification count updated");

            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notification-count",
                    response
            );

            log.debug("Sent notification count update to user: {} (count: {})", userEmail, unreadCount);

        } catch (Exception e) {
            log.error("Failed to send notification count update to user {}: {}", userEmail, e.getMessage(), e);
        }
    }
}