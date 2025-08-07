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
import java.util.Scanner;

@SpringBootApplication
public class RunClient {
    private static final Logger logger = LoggerFactory.getLogger(RunClient.class);

    private final ClientManager clientManager;
    private final LanternaUI lanternaUI;
    private final CmdParser cmdParser;
    private volatile boolean running = true;

    public RunClient() throws IOException {
        // Initialize Lanterna UI
        this.lanternaUI = new LanternaUI();
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
            lanternaUI.showWelcome();

            // Shutdown hook for graceful cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            mainLoop();
        } catch (Exception e) {
            logger.error("Application error", e);
            lanternaUI.showError("Application error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void mainLoop() {
        while(running) {
            try {
                // Show appropriate prompt based on mode
                if (lanternaUI.isInChatMode()) {
                    lanternaUI.showChatPrompt();
                } else {
                    lanternaUI.showMainPrompt();
                }

                // Read input with visual feedback
                String input = readInputWithEcho();

                if (input.equalsIgnoreCase("exit")) {
                    running = false;
                    break;
                }

                // Echo the command back to show what was typed
                if (lanternaUI.isInChatMode()) {
                    // In chat mode, if it's not a /exit command, show as sent message
                    if (!input.startsWith("/")) {
                        lanternaUI.showSentMessage(input);
                    } else {
                        System.out.println("│  > " + input);
                    }
                } else {
                    // In command mode, show the command that was entered
                    System.out.println("│  > " + input);
                }

                // Process command using existing parser
                cmdParser.parseAndExecute(input, clientManager);

            } catch (Exception e) {
                logger.error("Error in main loop", e);
                lanternaUI.showError("Error: " + e.getMessage());
            }
        }
    }

    private String readInputWithEcho() throws IOException {
        StringBuilder input = new StringBuilder();

        while (true) {
            try {
                // Check if input is available
                if (System.in.available() > 0) {
                    int ch = System.in.read();

                    if (ch == '\n' || ch == '\r') {
                        System.out.println(); // Move to next line
                        return input.toString();
                    } else if (ch == 127 || ch == 8) { // Backspace
                        if (input.length() > 0) {
                            input.deleteCharAt(input.length() - 1);
                            System.out.print("\b \b"); // Erase character
                        }
                    } else if (ch >= 32 && ch <= 126) { // Printable characters
                        input.append((char) ch);
                        System.out.print((char) ch); // Echo character
                    }
                }

                Thread.sleep(10); // Small delay to prevent busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return input.toString();
            }
        }
    }

    private void shutdown() {
        try {
            running = false;
            lanternaUI.showGoodbye();
            lanternaUI.cleanup();
            logger.info("Application shutdown complete");
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }
}