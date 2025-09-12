package com.autoecole.controller;

import com.autoecole.dto.common.WebSocketResponse;
import com.autoecole.model.User;
import com.autoecole.service.AuthenticationHelper;
import com.autoecole.service.NotificationService;
import com.autoecole.service.WebSocketNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@AllArgsConstructor
public class WebSocketController {

    private final NotificationService notificationService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final AuthenticationHelper authenticationHelper;

    /**
     * Handle client connection and send initial data
     */
    @MessageMapping("/connect")
    @SendToUser("/queue/connection")
    public WebSocketResponse<String> handleConnect(Principal principal) {
        log.info("WebSocket client connected: {}", principal.getName());

        // Send welcome message and initial notification count
        User currentUser = authenticationHelper.getCurrentUser();
        if (currentUser != null) {
            long unreadCount = notificationService.getUnreadNotificationCount(currentUser);
            webSocketNotificationService.sendNotificationCountUpdate(currentUser.getEmail(), unreadCount);
        }

        return WebSocketResponse.update("Connected successfully", "Welcome to AutoEcole notifications!");
    }

    /**
     * Handle client requests for notification count
     */
    @MessageMapping("/notifications/count")
    @SendToUser("/queue/notification-count")
    public WebSocketResponse<Long> getNotificationCount(Principal principal) {
        try {
            User currentUser = authenticationHelper.getCurrentUser();
            if (currentUser != null) {
                long unreadCount = notificationService.getUnreadNotificationCount(currentUser);
                return WebSocketResponse.update(unreadCount, "Notification count retrieved");
            } else {
                return WebSocketResponse.update(0L, "No user found");
            }
        } catch (Exception e) {
            log.error("Error getting notification count for user {}: {}", principal.getName(), e.getMessage(), e);
            return WebSocketResponse.update(0L, "Error retrieving notification count");
        }
    }

    /**
     * Handle mark notification as read requests
     */
    @MessageMapping("/notifications/markRead")
    @SendToUser("/queue/updates")
    public WebSocketResponse<?> markNotificationAsRead(Long notificationId, Principal principal) {
        try {
            notificationService.markAsRead(notificationId);

            // Send updated notification count
            User currentUser = authenticationHelper.getCurrentUser();
            if (currentUser != null) {
                long unreadCount = notificationService.getUnreadNotificationCount(currentUser);
                webSocketNotificationService.sendNotificationCountUpdate(currentUser.getEmail(), unreadCount);
            }

            return WebSocketResponse.update("success", "Notification marked as read");

        } catch (Exception e) {
            log.error("Error marking notification {} as read for user {}: {}",
                    notificationId, principal.getName(), e.getMessage(), e);
            return WebSocketResponse.error("Failed to mark notification as read");
        }
    }

    /**
     * Handle mark all notifications as read requests
     */
    @MessageMapping("/notifications/markAllRead")
    @SendToUser("/queue/updates")
    public WebSocketResponse<?> markAllNotificationsAsRead(Principal principal) {
        try {
            User currentUser = authenticationHelper.getCurrentUser();
            if (currentUser != null) {
                notificationService.markAllAsRead(currentUser);

                // Send updated notification count (should be 0)
                webSocketNotificationService.sendNotificationCountUpdate(currentUser.getEmail(), 0L);
            }

            return WebSocketResponse.update("success", "All notifications marked as read");

        } catch (Exception e) {
            log.error("Error marking all notifications as read for user {}: {}",
                    principal.getName(), e.getMessage(), e);
            return WebSocketResponse.error("Failed to mark all notifications as read");
        }
    }

    /**
     * Handle ping requests (for connection health check)
     */
    @MessageMapping("/ping")
    @SendToUser("/queue/pong")
    public WebSocketResponse<String> handlePing() {
        return WebSocketResponse.update("pong", "Connection is alive");
    }
}