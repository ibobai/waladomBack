package com.phanta.waladom.verification.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {



    @Autowired
    private final EmailVerificationCodeRepository verificationCodeRepository;

    @Value("${resend.api.key}")
    private String apiKey;

    private final String resendApiUrl = "https://api.resend.com/emails";
    private final String contactEmail = "contact@waladom.org";

    @Autowired
    private final RestTemplate restTemplate;

    private static final int CODE_EXPIRY_TIME = 5 * 60 * 1000; // 3 minutes in milliseconds

    @Autowired
    public EmailService(EmailVerificationCodeRepository verificationCodeRepository, RestTemplate restTemplate) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.restTemplate = restTemplate;
    }

    // Method to generate a 6-digit random code
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(code);
    }
    public Map<String, Object> sendVerificationCode(String toEmail) {
        // Check if the email already exists in the database
        EmailVerificationCode existingRecord = verificationCodeRepository.findByEmail(toEmail);

        if (existingRecord != null) {
            if (existingRecord.isVerified()) {
                return Map.of("send", false, "message", "Email is already verified. No need to send another code.");
            } else {
                // If the email exists but is not verified, update the verification code and expiration
                String newCode = generateCode();
                Instant now = Instant.now();
                Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

                existingRecord.setVerificationCode(newCode);
                existingRecord.setCreatedAt(now);
                existingRecord.setExpiresAt(expiresAt);

                verificationCodeRepository.save(existingRecord);

                return sendEmail(toEmail, newCode);
            }
        }

        // Generate a new 6-digit code for a new email
        String code = generateCode();
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(toEmail);
        verificationCode.setVerificationCode(code);
        verificationCode.setCreatedAt(now);
        verificationCode.setExpiresAt(expiresAt);
        verificationCode.setVerified(false);

        verificationCodeRepository.save(verificationCode);

        return sendEmail(toEmail, code);
    }

    // Helper method to send the email
    private Map<String, Object> sendEmail(String toEmail, String code) {
        String subject = "Email Verification Code";
        String messageText = "Please use the following code to verify your email: " + code;

        Map<String, Object> emailPayload = Map.of(
                "from", "contact@waladom.org",
                "to", toEmail,
                "subject", subject,
                "text", messageText
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);
        String resendApiUrl = "https://api.resend.com/emails";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    resendApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                return Map.of("send", true, "message", "Verification code sent successfully");
            } else {
                return Map.of("send", false, "message", "Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            return Map.of("send", false, "message", "Error sending email: " + e.getMessage());
        }
    }

    public Map<String, Object> verifyCode(String code, String email) {
        // Find the verification code record in the database
        EmailVerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode != null) {
            Instant now = Instant.now();

            if (verificationCode.isVerified()) {
                return Map.of("verified", false, "message", "Email is already verified.");
            }

            if (verificationCode.getExpiresAt().isBefore(now)) {
                return Map.of("verified", false, "message", "Code has expired.");
            }

            if (verificationCode.getVerificationCode().equals(code)) {
                verificationCode.setVerified(true);
                verificationCodeRepository.save(verificationCode);
                return Map.of("verified", true, "message", "Email verified successfully!");
            }

            return Map.of("verified", false, "message", "Invalid code.");
        }

        return Map.of("verified", false, "message", "No verification request found for this email.");
    }



    public Map<String, Object> sendContactMessage(String email, String phoneNumber, String firstName, String lastName, String subject, String message) {
        String formattedMessage = String.format(
                "New contact request:\n\n" +
                        "Name: %s %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n\n" +
                        "Message:\n%s",
                firstName, lastName, email, phoneNumber, message
        );

        Map<String, Object> emailPayload = Map.of(
                "from", contactEmail,
                "to", contactEmail,  // Sending to our own email
                "subject", subject,
                "text", formattedMessage
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    resendApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                return Map.of("send", true, "message", "Your message has been sent successfully.");
            } else {
                return Map.of("send", false, "message", "Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            return Map.of("send", false, "message", "Error sending email: " + e.getMessage());
        }
    }

    public boolean isValidRequest(Map<String, String> request) {
        return request.containsKey("email") && StringUtils.hasText(request.get("email")) &&
                request.containsKey("phoneNumber") && StringUtils.hasText(request.get("phoneNumber")) &&
                request.containsKey("firstName") && StringUtils.hasText(request.get("firstName")) &&
                request.containsKey("lastName") && StringUtils.hasText(request.get("lastName")) &&
                request.containsKey("subject") && StringUtils.hasText(request.get("subject")) &&
                request.containsKey("message") && StringUtils.hasText(request.get("message"));
    }
}
