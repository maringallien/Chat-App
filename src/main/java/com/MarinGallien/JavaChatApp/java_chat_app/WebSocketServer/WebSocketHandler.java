package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {
    // Called when a WebSocket connection is opened
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    // Called when a WebSocket connection is closed
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }
}
