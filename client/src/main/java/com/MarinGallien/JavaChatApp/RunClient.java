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

import java.util.Scanner;

@SpringBootApplication
public class RunClient {
    private static final Logger logger = LoggerFactory.getLogger(RunClient.class);

    private final ClientManager clientManager;
    private final ConsoleUI consoleUI;
    private final CmdParser cmdParser;
    private final Scanner scanner;
    private volatile boolean running = true;

    public RunClient() {
        // Init UI and command parser
        this.consoleUI = new ConsoleUI();
        this.cmdParser = new CmdParser();
        this.scanner = new Scanner(System.in);

        // Initialize API services
        APIClient apiClient = new APIClient();
        APIService apiService = new APIService(apiClient);

        // Initialize WebSocket services
        WebSocketClient webSocketClient = new WebSocketClient();
        ChatService chatService = new ChatService(webSocketClient);

        this.clientManager = new ClientManager(apiService, chatService, consoleUI);

        chatService.setMessageListener(consoleUI);
    }

    public static void main(String[] args) {
        // Configure Spring Boot to not start a web server
        System.setProperty("spring.main.web-application-type", "none");

        logger.info("Starting Java Chat Client...");

        // Start spring context
        ConfigurableApplicationContext context = SpringApplication.run(RunClient.class, args);

        // Get the main application bean and run it
        RunClient app = context.getBean(RunClient.class);
        app.run();

        // Cleanup and exit
        context.close();
        System.exit(0);
    }

    public void run() {
        try {
            consoleUI.showWelcome();

            // Shutdown hook for graceful cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            mainLoop();
        } catch (Exception e) {
            logger.error("Application error", e);
            consoleUI.showError("Application error: " + e.getMessage());
        }
    }

    private void mainLoop() {
        while(running) {
            try {
                // Show appropriate prompt
                consoleUI.showChatPrompt();

                // Read user input
                String input = scanner.nextLine().trim();

                // Handle empty input
                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("quit")) {
                    break;
                }

                handleUserInput(input);
            } catch (Exception e) {
                logger.error("Error processing user input", e);
                consoleUI.showError("Error: " + e.getMessage());
            }
        }
    }

    private void handleUserInput(String input) {
        cmdParser.parseAndExecute(input, clientManager);
    }

    private void shutdown() {
        logger.info("Shutting down application...");
        running = false;

        try {
            // Disconnect from chat service gracefully
            if (clientManager != null) {
                logger.info("Disconnecting from chat service");
                clientManager.disconnect();
            }

            // Close scanner
            if (scanner != null) {
                scanner.close();
            }

            logger.info("Application shutdown complete");
        } catch (Exception e) {
            logger.error("Error during shutdown: {}", e.getMessage());
        }
    }
}
