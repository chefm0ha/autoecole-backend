package com.autoecole.websocket;

import com.autoecole.config.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Get authentication from session attributes (set during handshake)
            Authentication authentication = (Authentication) Objects.requireNonNull(accessor.getSessionAttributes()).get("AUTHENTICATION");

            if (authentication != null && authentication.isAuthenticated()) {
                accessor.setUser(authentication);

                // Log the connection
                String userEmail = null;
                if (authentication.getPrincipal() instanceof CustomUserDetails) {
                    userEmail = ((CustomUserDetails) authentication.getPrincipal()).getUser().getEmail();
                }
                log.info("WebSocket CONNECT for user: {}", userEmail != null ? userEmail : authentication.getName());
            } else {
                log.warn("WebSocket CONNECT failed - no valid authentication");
                return null; // Reject the connection
            }
        }

        return message;
    }
}