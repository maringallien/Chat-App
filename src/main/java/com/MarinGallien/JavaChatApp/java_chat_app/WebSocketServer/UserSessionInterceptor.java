package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

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
                logger.info("Set userId {} in session", userId);
            } else {
                logger.warn("No userId provided in connection headers");
            }
        }
        return message;
    }
}
