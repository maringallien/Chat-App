package com.MarinGallien.JavaChatApp;

public class UserSession {

    private static UserSession instance;

    private String userId;
    private String username;
    private String email;
    private String jwtToken;
    private String serverEndpoint = "localhost:8080";

    public  void initUserSession(String userId, String jwtToken) {
        this.userId = userId;
        this.jwtToken = jwtToken;
    }

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }
    public String getUserId() {return userId;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getServerEndpoint() {return serverEndpoint;}
    public String getJwtToken() {return jwtToken;}
    public String getHttpBaseUrl() {return "http://" + serverEndpoint;}
    public String getWsBaseUrl() {return "ws://" + serverEndpoint + "/ws";}


    public void setUserId(String userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
    public void setEmail(String email) {this.email = email;}
    public void setJwtToken(String jwtToken) {this.jwtToken = jwtToken;}
    public void setServerEndpoint(String serverEndpoint) {this.serverEndpoint = serverEndpoint;}
}
