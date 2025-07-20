package Enums;

public enum ChatType {
    SINGLE("SINGLE"),
    GROUP("GROUP");

    // Variable to store enum string representation for JSON
    private final String value;

    // Constructor creates string representation of enum for JSON
    ChatType(String value) {
        this.value = value;
    }

    // Getter for JSON serialization
    public String getValue() {
        return value;
    }

    // Custom toString method
    @Override
    public String toString() {
        return value;
    }

    public static ChatType fromString(String value) {
        // Loop through all enum values looking for a match
        for (ChatType type : ChatType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        // If we get here, the string doesn't match any chat type
        throw new IllegalArgumentException("Unknown chat type: " + value);
    }
}
