package com.MarinGallien.JavaChatApp.DTOs.WebsocketMessages;

import com.MarinGallien.JavaChatApp.Enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * This is the base message/DTO (Data Transfer Object) class. All subclasses will inherit from this class.
*/

public class BaseMessage {
    // Parameters
    @NotBlank(message = "Message ID cannot be blank")
    private String messageID;

    @NotNull(message = "Message type cannot be blank")
    private MessageType type;

    @NotBlank(message = "Sender ID cannot be blank")
    private String senderID;

    @NotBlank(message = "Timestamp ID cannot be blank")
    private Long timestamp;

    // Default constructor for Jackson - used when received messages need deserialization from JSON
    protected BaseMessage() {
        this.messageID = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toEpochMilli();
    }

    // Constructor with required fields - used when creating DTO objects in code
    protected BaseMessage(MessageType type, String senderID) {
        this();
        this.type = type;
        this.senderID = senderID;
    }

    // Getters
    public String getMessageID() {return messageID;}
    public MessageType getType() {return type;}
    public String getSenderID() {return senderID;}
    public Long getTimestamp() {return timestamp;}

    // Setters
    public void setMessageID(String messageID) {this.messageID = messageID;}
    public void setType(MessageType type) {this.type = type;}
    public void setSenderID(String senderID) {this.senderID = senderID;}
    public void setTimestamp(Long timestamp) {this.timestamp = timestamp;}

    // Equals function checks for equality between 2 DTO IDs.
    @Override
    public boolean equals(Object obj){
        // Check for equality
        if (this == obj) return true;
        // Check for type equality
        if (obj == null || this.getClass() != obj.getClass()) return false;
        // Convert to BaseMessage to access fields and check message ID equality
        BaseMessage that = (BaseMessage) obj;
        return messageID != null ? messageID.equals(that.messageID) : that.messageID == null;
    }

    // Function used to generate hashcodes from MessageID for fast hashMap lookup in O(1) time
    @Override
    public int hashCode() {
        return messageID != null ? messageID.hashCode() : 0;
    }

    // Basic toString function
    @Override
    public String toString() {
        return "BaseMessage{" +
                "messageId='" + messageID + '\'' +
                ", type='" + type + '\'' +
                ", senderID='" + senderID + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
