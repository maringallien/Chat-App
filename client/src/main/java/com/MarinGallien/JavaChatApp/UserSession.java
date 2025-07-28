package com.MarinGallien.JavaChatApp;

public class UserSession {
    // Authentication information
    private static String userId;
    private static String username;
    private static String email;
    private static String jwtToken;
    private static String serverEndpoint = "localhost:8080";

    public static void initUserSession(String userId, String jwtToken) {
        UserSession.userId = userId;

        UserSession.jwtToken = jwtToken;
    }

    public static String getUserId() {return userId;}
    public static String getUsername() {return username;}
    public static String getEmail() {return email;}
    public static String getServerEndpoint() {return serverEndpoint;}
    public static String getJwtToken() {return jwtToken;}
    public static String getHttpBaseUrl() {return "http://" + serverEndpoint;}
    public static String getWsBaseUrl() {return "ws://" + serverEndpoint + "/ws";}


    public static void setUserId(String userId) {UserSession.userId = userId;}
    public static void setUsername(String username) {UserSession.username = username;}
    public static void setEmail(String email) {UserSession.email = email;}
    public static void setJwtToken(String jwtToken) {UserSession.jwtToken = jwtToken;}
    public static void setServerEndpoint(String serverEndpoint) {UserSession.serverEndpoint = serverEndpoint;}
}
