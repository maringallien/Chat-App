package DTOs.HTTPMessages.Requests;

public interface ApiReqResInterface {
    // Marker interface for all API responses
    default long timestamp() {
        return java.time.Instant.now().toEpochMilli();
    }
}