package com.MarinGallien.JavaChatApp.java_chat_app.DTOs.Enums;

public enum OnlineStatusType {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE");

    // Stores string representation of enum for JSON
    private final String value;

    // Constructor creates string representation of enum for JSON
    OnlineStatusType(String value) {this.value = value;}

    // Returns the string value of message type for JSON serialization
    public String getValue() {return value;}

    // Overrides toString to return custom string value for the enum name
    @Override
    public String toString() {return value;}

    // Method used by JSON for deserialization of string into corresponding MessageType enum\
    public static OnlineStatusType fromString(String value) {
        for (OnlineStatusType status : OnlineStatusType.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        // If we get there, the string doesn't match any message types, probably a typo
        throw new IllegalArgumentException("Unknown presence status: " + value);
    }
}