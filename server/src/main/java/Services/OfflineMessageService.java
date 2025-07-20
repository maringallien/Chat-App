package Services;

import DTOs.WebsocketMessages.WebSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfflineMessageService {

    private static final Logger logger = LoggerFactory.getLogger(OfflineMessageService.class);
    private static final String OFFLINE_MESSAGE_PREFIX = "offline_message";
    private static final long MAX_USER_MESSAGES = 1000;
    private static final int BATCH_SIZE = 50;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public OfflineMessageService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean storeOfflineMessage(String userId, WebSocketMessage message) {
        try {
            // Validate input parameters
            if (!validateId(userId)) {
                logger.warn("Cannot queue message: user ID is null or empty");
                return false;
            }

            if (message == null) {
                logger.warn("Cannot queue message: message is null");
                return false;
            }

            // Create redis key for this user's offline messages
            String redisKey = OFFLINE_MESSAGE_PREFIX + userId.trim();

            // Make sure user doesn't already have too many pending messages
            Long queueCount = redisTemplate.opsForList().size(redisKey);
            if (queueCount != null && queueCount >= MAX_USER_MESSAGES) {
                logger.warn("Cannot queue message for user {}: queue is full ({} messages)", userId, queueCount);
                return false;
            }

            // Serialize to Json
            String messageJson = objectMapper.writeValueAsString(message);

            // Store messages in Redis FIFO queue
            redisTemplate.opsForList().rightPush(redisKey, messageJson);

            // Set key expiry date to prevent indefinite storage
            redisTemplate.expire(redisKey, java.time.Duration.ofDays(7));

            logger.info("Successful queued message for user {} (queue size {})", userId, queueCount);

            return true;

        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message for user {}: {}", userId, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error queuing message for offline user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public List <WebSocketMessage> retrievePendingMessages(String userId) {
        List<WebSocketMessage> messages = new ArrayList<>();

        try {
            // Validate input parameters
            if (!validateId(userId)) {
                logger.warn("Could not retrieve pending messages: user ID is null or empty");
                return messages;
            }

            // Compose the key
            String redisKey = OFFLINE_MESSAGE_PREFIX + userId.trim();

            // Check that user has any pending messages
            Long totalMessages = redisTemplate.opsForList().size(redisKey);
            if (totalMessages == null || totalMessages == 0) {
                logger.info("No pending messages for user {}", userId);
                return messages;
            }

            // Process messages in batches for balance between performance and memory efficiency
            int processedCount = 0;
            int batchNum = 1;

            while (true) {
                List<String> batch = redisTemplate.opsForList().range(redisKey, 0, BATCH_SIZE - 1);

                if (batch == null || batch.isEmpty()) {
                    break;
                }

                // Deserialize and add messages to messages list
                for (String messageJson : batch) {
                    try{
                        WebSocketMessage message = objectMapper.readValue(messageJson, WebSocketMessage.class);
                        messages.add(message);
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to deserialize message in batch {} for user {}", batchNum, userId);
                    }
                }

                // Remove messages from redis queue
                for (int i = 0; i < batch.size(); i++) {
                    redisTemplate.opsForList().leftPop(redisKey);
                }

                // Safety check to prevent infinite loops
                if (processedCount >= MAX_USER_MESSAGES) {
                    logger.warn("Reached Maximum message queuing limit while processing for user {}", userId);
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Error retrieving pending messages for user {}. {}", userId, e.getMessage());
        }

        return messages;
    }

    private boolean validateId(String userId) {
        return userId != null && !userId.trim().isEmpty();
    }

    public boolean hasPendingMessages(String userId) {
        try {
            // Perform input parameter validation
            if (!validateId(userId)) {
                return false;
            }

            String redisKey = OFFLINE_MESSAGE_PREFIX + userId;

            Long messageCount = redisTemplate.opsForList().size(redisKey);

            return messageCount != null && messageCount > 0;

        } catch (Exception e) {
            logger.error("Error checking pending messages for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
