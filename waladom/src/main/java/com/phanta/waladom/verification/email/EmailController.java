package com.phanta.waladom.verification.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(EmailController.class);

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

        logger.info("Received request to send verification code to email: {}", toEmail);

        if (toEmail == null) {
            logger.error("Email is missing in the request.");
            return ResponseEntity.badRequest().body(Map.of("send", false, "message", "Email is required"));
        }

        Map<String, Object> response = emailService.sendVerificationCode(toEmail);
        logger.info("Verification code sent to email: {}", toEmail);
        return ResponseEntity.ok(response);
    }

    // Endpoint to verify the entered code
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String email = request.get("email");

        logger.info("Received request to verify code for email: {}", email);

        if (code == null) {
            logger.error("Verification code is missing in the request for email: {}", email);
            return ResponseEntity.badRequest().body(Map.of("verified", false, "message", "Code is required"));
        }

        Map<String, Object> response = emailService.verifyCode(code, email);
        if (response.get("verified").equals(Boolean.TRUE)) {
            logger.info("Verification successful for email: {}", email);
        } else {
            logger.warn("Verification failed for email: {}", email);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contact/waladom")
    public ResponseEntity<Map<String, Object>> contactWaladom(@RequestBody Map<String, String> request) {
        // Validate request data
        if (!emailService.isValidRequest(request)) {
            logger.error("Invalid contact request from: {}", request.get("email"));
            return ResponseEntity.badRequest().body(Map.of("send", false, "message", "Invalid request data. All fields are required."));
        }

        // Extract fields from request
        String email = request.get("email");
        String phoneNumber = request.get("phoneNumber");
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String subject = request.get("subject");
        String message = request.get("message");

        logger.info("Received contact request from email: {}", email);
        logger.debug("Contact request details: Phone: {}, First Name: {}, Last Name: {}, Subject: {}", phoneNumber, firstName, lastName, subject);

        // Call the service method to send an email
        Map<String, Object> response = emailService.sendContactMessage(email, phoneNumber, firstName, lastName, subject, message);

        if ((boolean) response.get("send")) {
            logger.info("Contact message sent successfully to: {}", email);
        } else {
            logger.error("Failed to send contact message to: {}", email);
        }

        return ResponseEntity.ok(response);
    }
}
