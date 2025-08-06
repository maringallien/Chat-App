package com.MarinGallien.JavaChatApp.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class UserSessionInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = accessor.getFirstNativeHeader("userId");

            if (userId != null && !userId.trim().isEmpty()) {
                accessor.getSessionAttributes().put("userId", userId.trim());

                // THIS IS THE KEY FIX - Set the Principal for Spring's routing
                accessor.setUser(new SimplePrincipal(userId.trim()));

                logger.info("Set userId {} as Principal", userId);
            }
        }
        return message;
    }

    // Simple Principal implementation
    public class SimplePrincipal implements Principal {
        private final String name;

        public SimplePrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
