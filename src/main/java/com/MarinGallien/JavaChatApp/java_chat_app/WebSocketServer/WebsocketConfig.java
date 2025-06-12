package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandler(WebSocketHandlerRegistry registry) {
        /**
        Sample handler:
        registry.addHandler(webSocketHandler, "/chat")
                .setAllowedOrigins("*") // allow all origins for development
                .withSockJS(); // Enable sockJS fallback for browsers that don't support websockets
         */
    }
}
