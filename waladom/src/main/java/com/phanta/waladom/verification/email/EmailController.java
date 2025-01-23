package com.phanta.waladom.verification.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/verification/email")
public class EmailController {

    @Autowired
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Endpoint to send the verification code to the user
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String toEmail = request.get("email");

        if (toEmail == null) {
            return ResponseEntity.badRequest().body(Map.of("send", false, "message", "Email is required"));
        }

        Map<String, Object> response = emailService.sendVerificationCode(toEmail);
        return ResponseEntity.ok(response);
    }

    // Endpoint to verify the entered code
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String email = request.get("email");

        if (code == null) {
            return ResponseEntity.badRequest().body(Map.of("verified", false, "message", "Code is required"));
        }

        Map<String, Object> response = emailService.verifyCode(code, email);
        return ResponseEntity.ok(response);
    }
}
