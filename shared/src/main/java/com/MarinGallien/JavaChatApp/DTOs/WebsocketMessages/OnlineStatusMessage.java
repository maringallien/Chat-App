package com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OnlineStatusMessage extends BaseMessage {
    // Parameters
    @NotBlank(message = "Sender name cannot be blank")
    private String username;

    @NotNull(message = "Status cannot be null")
    private OnlineStatus status;

    // Default constructor
    public OnlineStatusMessage() {super();}

    // Constructor for creating online presence messages
    public OnlineStatusMessage(OnlineStatus status, String senderID, String username){
        super(MessageType.ONLINE_PRESENCE, senderID);
        this.status = status;
        this.username = username;
    }

    // Getters
    public OnlineStatus getStatus() {return status;}
    public String getUsername() {return username;}

    // Setters
    public void setStatus(OnlineStatus status) {this.status = status;}
    public void setUsername(String username) {this.username = username;}

    public String toString() {
        return "PresenceMessage{" +
                "status=" + status +
                ", " + super.toString() +
                '}';
    }
}
