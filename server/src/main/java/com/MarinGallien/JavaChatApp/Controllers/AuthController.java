package com.MarinGallien.JavaChatApp.Controllers;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.AuthRequests.LoginRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.AuthRequests.RegisterRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.AuthResponses.LoginResponse;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.GenericResponse;
import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Services.AuthService.JWTService;
import com.MarinGallien.JavaChatApp.Services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JWTService jwtService;

    public AuthController(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {

        try {
            // Return if errors in input
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Invalid input"));
            }

            // Delegate to UserService
            User user = userService.createUser(request.username(), request.email(), request.password());

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Registration failed"));
            }

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "User registered successfully"));

        } catch (Exception e) {
            logger.error("Failed to process register request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal Server Error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult) {

        try {
            // Return if errors in input
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(new LoginResponse(false, "Invalid input", null, null,null));
            }

            // Delegate to UserService
            String token = userService.login(request.email(), request.password());
            String username = userService.getUsernameByEmail(request.email());

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "Invalid credentials", null, null,null));
            }

            return ResponseEntity.ok()
                    .body(new LoginResponse(true, "Login successful", jwtService.extractUserId(token), username, token));

        } catch (Exception e) {
            logger.error("Failed to process login request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(false, "Internal Server Error", null, null,null));
        }
    }

}
