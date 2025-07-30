package com.MarinGallien.JavaChatApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RunServer {

    private static final Logger logger = LoggerFactory.getLogger(RunServer.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting Java Chat Server Application...");

            ConfigurableApplicationContext context = SpringApplication.run(RunServer.class, args);
            Environment env = context.getEnvironment();

            String serverPort = env.getProperty("server.port", "8080");
            String appName = env.getProperty("spring.application.name", "java-chat-server");

            logger.info("=================================================================");
            logger.info("Application '{}' is running!", appName);
            logger.info("Server started on port: {}", serverPort);
            logger.info("WebSocket endpoint: ws://localhost:{}/ws", serverPort);
            logger.info("Profile(s): {}", String.join(", ", env.getActiveProfiles()));
            logger.info("=================================================================");

            // Add graceful shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down Java Chat Server Application...");
                context.close();
                logger.info("Application shutdown complete.");
            }));

        } catch (Exception e) {
            logger.error("Failed to start Java Chat Server Application", e);
            System.exit(1);
        }
    }
}