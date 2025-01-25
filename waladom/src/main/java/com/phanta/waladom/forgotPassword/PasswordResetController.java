package com.phanta.waladom.forgotPassword;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/password")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/reset")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(
            @RequestHeader("identifier") String identifier,
            @RequestHeader("Connection-Method") String connectionMethod) {

        // Supported connection methods
        if (!connectionMethod.equalsIgnoreCase("email") && !connectionMethod.equalsIgnoreCase("phone")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sent", false,
                    "message", "Unknown connection method"
            ));
        }

        // Call the service to send the verification code
        Map<String, Object> response = passwordResetService.sendCode(identifier, connectionMethod);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateResetCode(
            @RequestHeader("identifier") String identifier,
            @RequestHeader("Connection-Method") String connectionMethod, @RequestHeader("code") String code) {

        // Supported connection methods
        if (!connectionMethod.equalsIgnoreCase("email") && !connectionMethod.equalsIgnoreCase("phone")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sent", false,
                    "message", "Unknown connection method"
            ));
        }

        // Call the service to send the verification code
        Map<String, Object> response = passwordResetService.validateCode(identifier, connectionMethod, code);

        return ResponseEntity.ok(response);
    }



}
