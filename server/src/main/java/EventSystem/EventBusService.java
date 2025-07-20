package EventSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventBusService {
    private static final Logger logger = LoggerFactory.getLogger(EventBusService.class);
    private final ApplicationEventPublisher eventPublisher;

    public EventBusService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishEvent(Object event) {
        try {
            if (event == null) {
                logger.warn("Failed to publish event: event is null");
                return;
            }
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            logger.error("Failed to publish even");
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
