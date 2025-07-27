package com.MarinGallien.JavaChatApp;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    // Authentication information
    private static String userId;
    private static String jwtToken;
    private static String serverUrl = "ws://localhost:8080";

    public static void initUserSession(String userId, String jwtToken) {
        UserSession.userId = userId;
        UserSession.jwtToken = jwtToken;
    }

    public static String getUserId() {return userId;}
    public static String getJwtToken() {return jwtToken;}
    public static String getServerUrl() {return serverUrl;}

    public static void setUserId(String userId) {UserSession.userId = userId;}
    public static void setJwtToken(String jwtToken) {UserSession.jwtToken = jwtToken;}
    public static void setServerUrl(String serverUrl) {UserSession.serverUrl = serverUrl;}
}
