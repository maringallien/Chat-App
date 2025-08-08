package com.MarinGallien.JavaChatApp;

import com.MarinGallien.JavaChatApp.API.APIClient;
import com.MarinGallien.JavaChatApp.API.APIService;
import com.MarinGallien.JavaChatApp.WebSocket.ChatService;
import com.MarinGallien.JavaChatApp.WebSocket.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class RunClient {
    private static final Logger logger = LoggerFactory.getLogger(RunClient.class);

    private final ClientManager clientManager;
    private final LanternaUI lanternaUI;  // Changed from ConsoleUI to LanternaUI
    private final CmdParser cmdParser;
    private volatile boolean running = true;

    public RunClient() throws IOException {
        // Initialize Lanterna UI first
        this.lanternaUI = new LanternaUI();  // Changed from ConsoleUI to LanternaUI
        this.cmdParser = new CmdParser();

        // Initialize API services
        APIClient apiClient = new APIClient();
        APIService apiService = new APIService(apiClient);

        // Initialize WebSocket services
        WebSocketClient webSocketClient = new WebSocketClient();
        ChatService chatService = new ChatService(webSocketClient);

        this.clientManager = new ClientManager(apiService, chatService, lanternaUI);

        chatService.setMessageListener(lanternaUI);
    }

    public static void main(String[] args) {
        // Configure Spring Boot to not start a web server
        System.setProperty("spring.main.web-application-type", "none");

        logger.info("Starting Java Chat Client with enhanced terminal UI...");

        try {
            // Start spring context
            ConfigurableApplicationContext context = SpringApplication.run(RunClient.class, args);

            // Get the main application bean and run it
            RunClient app = context.getBean(RunClient.class);
            app.run();

            // Cleanup and exit
            context.close();
            System.exit(0);
        } catch (Exception e) {
            logger.error("Failed to initialize terminal UI", e);
            System.err.println("Failed to start application: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        try {
            // Initialize Lanterna and show welcome
            lanternaUI.initialize();
            lanternaUI.showWelcome();

            // Shutdown hook for graceful cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            mainLoop();
        } catch (Exception e) {
            logger.error("Application error", e);
            if (lanternaUI != null) {
                lanternaUI.showError("Application error: " + e.getMessage());
            }
        } finally {
            shutdown();
        }
    }

    private void mainLoop() {
        while(running) {
            try {
                // Show appropriate prompt and read input using Lanterna
                String input;
                if (lanternaUI.isInChatMode()) {
                    input = lanternaUI.readChatInput();
                } else {
                    input = lanternaUI.readCommandInput();
                }

                if (input == null || input.equalsIgnoreCase("exit")) {
                    running = false;
                    break;
                }

                // Process command using existing parser
                cmdParser.parseAndExecute(input, clientManager);

            } catch (Exception e) {
                logger.error("Error in main loop", e);
                if (lanternaUI != null) {
                    lanternaUI.showError("Error: " + e.getMessage());
                }
            }
        }
    }

    private void shutdown() {
        try {
            running = false;
            if (lanternaUI != null) {
                lanternaUI.showGoodbye();
                lanternaUI.cleanup();
            }
            logger.info("Application shutdown complete");
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }
}