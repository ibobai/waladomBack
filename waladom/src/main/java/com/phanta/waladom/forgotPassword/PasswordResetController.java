package com.phanta.waladom.forgotPassword;

import com.phanta.waladom.user.UserRequestDTO;
import com.phanta.waladom.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/password")
public class PasswordResetController {

    private static final Logger logger = LogManager.getLogger(PasswordResetController.class);
    private final PasswordResetService passwordResetService;
    private final UserService userService;

    public PasswordResetController(PasswordResetService passwordResetService, UserService userService) {
        this.passwordResetService = passwordResetService;
        this.userService = userService;
    }

    @GetMapping("/reset")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(
            @RequestHeader("identifier") String identifier,
            @RequestHeader("Connection-Method") String connectionMethod) {

        logger.info("Received password reset request for identifier: {}, connectionMethod: {}", identifier, connectionMethod);

        if (!connectionMethod.equalsIgnoreCase("email") && !connectionMethod.equalsIgnoreCase("phone")) {
            logger.warn("Invalid connection method provided: {}", connectionMethod);
            return ResponseEntity.badRequest().body(Map.of(
                    "send", false,
                    "message", "Unknown connection method"
            ));
        }
        if(connectionMethod.equalsIgnoreCase("email")){
            identifier = identifier.toLowerCase();
        }
        Map<String, Object> response = passwordResetService.sendCode(identifier, connectionMethod);

        logger.info("Password reset request processed for identifier: {}, response: {}", identifier, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateResetCode(
            @RequestHeader("identifier") String identifier,
            @RequestHeader("Connection-Method") String connectionMethod,
            @RequestHeader("code") String code) {

        logger.info("Received reset code validation request for identifier: {}, connectionMethod: {}, code: {}", identifier, connectionMethod, code);

        if (!connectionMethod.equalsIgnoreCase("email") && !connectionMethod.equalsIgnoreCase("phone")) {
            logger.warn("Invalid connection method provided: {}", connectionMethod);
            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "message", "Unknown connection method"
            ));
        }

        if(connectionMethod.equalsIgnoreCase("email")){
            identifier = identifier.toLowerCase();
        }
        Map<String, Object> response = passwordResetService.validateCode(identifier, connectionMethod, code);

        logger.info("Reset code validation result for identifier: {}, response: {}", identifier, response);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @PathVariable("id") String userId,
            @RequestBody Map<String, String> requestBody) {

        logger.info("Received password update request for user ID: {}", userId);

        // Extract the new password
        String newPassword = requestBody.get("password");
        if (newPassword == null || newPassword.isBlank()) {
            logger.warn("Password update request failed: Empty password for user ID: {}", userId);
            return ResponseEntity.badRequest().body(Map.of(
                    "updated", false,
                    "message", "Password cannot be empty"
            ));
        }

        // Create a UserRequestDTO with the new password
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setPassword(newPassword);

        // Update the user's password
        try {
            userService.updateUser(userId, userRequestDTO);
            logger.info("Password successfully updated for user ID: {}", userId);
            return ResponseEntity.ok(Map.of(
                    "updated", true,
                    "message", "Password updated successfully"
            ));
        } catch (Exception e) {
            logger.error("Error updating password for user ID: {}", userId, e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "updated", false,
                    "message", "An error occurred while updating the password"
            ));
        }
    }

}
