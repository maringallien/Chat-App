package com.MarinGallien.JavaChatApp.java_chat_app.Enums;

public enum MessageType {
    TEXT_MESSAGE("TEXT_MESSAGE"),
    ONLINE_PRESENCE("ONLINE_PRESENCE");

    // Stores string representation of enum for JSON
    private final String value;

    // Constructor creates string representation of enum for JSON
    MessageType(String value) {this.value = value;}

    // Returns the string value of message type for JSON serialization
    public String getValue() {return value;}

    @Override
    public String toString() {return value;}
    
    // Method used by JSON for deserialization of string into corresponding MessageType enum\
    public static MessageType fromString(String value){
        // Loop through all enum values looking for match
        for (MessageType type : MessageType.values()){
            if (type.value.equals(value)){
                return type;
            }
        }
        // If we get there, the string doesn't match any message types, probably a typo
        throw new IllegalArgumentException("Unknown message type: " + value);
    }
}