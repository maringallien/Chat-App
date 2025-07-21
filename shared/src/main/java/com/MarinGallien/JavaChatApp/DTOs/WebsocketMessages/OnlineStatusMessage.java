package com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import com.MarinGallien.JavaChatApp.Enums.OnlineStatus;
import jakarta.validation.constraints.NotNull;

public class OnlineStatusMessage extends BaseMessage {
    // Parameters
    @NotNull(message = "Status cannot be null")
    private OnlineStatus status;

    // Default constructor
    public OnlineStatusMessage() {super();}

    // Constructor for creating online presence messages
    public OnlineStatusMessage(OnlineStatus status, String senderID){
        super(MessageType.ONLINE_PRESENCE, senderID);
        this.status = status;
    }

    // Getters
    public OnlineStatus getStatus() {return status;}

    // Setters
    public void setStatus(OnlineStatus status) {this.status = status;}

    public String toString() {
        return "PresenceMessage{" +
                "status=" + status +
                ", " + super.toString() +
                '}';
    }
}
