package Client;

import java.util.HashMap;
import java.util.Map;

/*
This class stores session information:
authentication info

 */
public class UserSession {
    // Authentication information
    private String userId;
    private String jwtToken;
    private String currentUsername;

    // Mappings
    private Map<String, String> userNameToUserId = new HashMap<>();
    private Map<String, String> chatNameToChatId = new HashMap<>();
    private Map<String, String> fileNameToFileId = new HashMap<>();

    // Need methods to update caches

    // Need lookup methods
}
