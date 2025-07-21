package com.MarinGallien.JavaChatApp.Controllers;

import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.UserRequests.UpdateEmailRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.UserRequests.UpdatePasswdRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Requests.UserRequests.UpdateUnameRequest;
import com.MarinGallien.JavaChatApp.DTOs.HTTPMessages.Responses.GenericResponse;
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
@RequestMapping("/api/user")
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update/username")
    public ResponseEntity<GenericResponse> updateUsername(
            @Valid @RequestBody UpdateUnameRequest request,
            BindingResult bindingResult) {

        try {
            // Return if errors in input
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Invalid input"));
            }

            // Delegate to UserService
            String newUname = userService.updateUsername(request.userId(), request.username());

            if (newUname == null) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Username update failed"));
            }

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Username updated successfully"));

        } catch (Exception e) {
            logger.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal Server Error"));
        }
    }


    @PostMapping("/update/email")
    public ResponseEntity<GenericResponse> updateEmail(
            @Valid @RequestBody UpdateEmailRequest request,
            BindingResult bindingResult) {

        try {
            // Return if errors in input
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Invalid input"));
            }

            // Delegate to UserService
            String newEmail = userService.updateEmail(request.userId(), request.email());

            if (newEmail == null) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Email update failed"));
            }

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Email updated successfully"));

        } catch (Exception e) {
            logger.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal Server Error"));
        }
    }


    @PostMapping("/update/password")
    public ResponseEntity<GenericResponse> updatePassword(
            @Valid @RequestBody UpdatePasswdRequest request,
            BindingResult bindingResult) {

        try {
            // Return if errors in input
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Invalid input"));
            }

            // Delegate to UserService
            boolean updated = userService.updatePassword(request.userId(), request.oldPassword(), request.newPassword());

            if (!updated) {
                return ResponseEntity.badRequest()
                        .body(new GenericResponse(false, "Password update failed"));
            }

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Password updated successfully"));

        } catch (Exception e) {
            logger.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(false, "Internal Server Error"));
        }
    }

}
