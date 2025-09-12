package com.autoecole.controller;

import com.autoecole.dto.response.NotificationDTO;
import com.autoecole.model.User;
import com.autoecole.service.AuthenticationHelper;
import com.autoecole.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@CrossOrigin
@AllArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;
    private final AuthenticationHelper authenticationHelper;

    /**
     * Get all notifications for the current user (for the initial load)
     */
    @GetMapping("/getUserNotifications")
    public List<NotificationDTO> getUserNotifications() {
        User currentUser = authenticationHelper.getCurrentUser();
        return notificationService.getNotificationsByUser(currentUser);
    }
}