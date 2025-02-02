package com.phanta.waladom.verification.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {


    private static final Logger logger = LogManager.getLogger(EmailService.class);


    @Autowired
    private final EmailVerificationCodeRepository verificationCodeRepository;

    @Value("${resend.api.key}")
    private String apiKey;

    private final String resendApiUrl = "https://api.resend.com/emails";
    private final String contactEmail = "contact@waladom.org";

    @Autowired
    private final RestTemplate restTemplate;

    private static final int CODE_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Autowired
    public EmailService(EmailVerificationCodeRepository verificationCodeRepository, RestTemplate restTemplate) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.restTemplate = restTemplate;
    }

    // Method to generate a 6-digit random code
    // Method to generate a 6-digit random code
    public String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit number
        logger.debug("Generated verification code: {}", code);
        return String.valueOf(code);
    }

    public Map<String, Object> sendVerificationCode(String toEmail) {
        logger.info("Initiating verification code process for email: {}", toEmail);

        // Check if the email already exists in the database
        EmailVerificationCode existingRecord = verificationCodeRepository.findByEmail(toEmail);

        if (existingRecord != null) {
            logger.info("Existing record found for email: {}", toEmail);
            if (existingRecord.isVerified()) {
                logger.info("Email is already verified. No need to send another code.");
                return Map.of("send", false, "message", "Email is already verified. No need to send another code.", "messageNumber", 1);
            } else {
                // If the email exists but is not verified, update the verification code and expiration
                logger.info("Email not verified. Generating a new verification code.");
                String newCode = generateCode();
                Instant now = Instant.now();
                Instant expiresAt = now.plusMillis(CODE_EXPIRY_TIME);

                existingRecord.setVerificationCode(newCode);
                existingRecord.setCreatedAt(now);
                existingRecord.setExpiresAt(expiresAt);

                verificationCodeRepository.save(existingRecord);
                logger.info("Updated verification code for email: {}", toEmail);

                return sendEmail(toEmail, newCode);
            }
        }

        // Generate a new 6-digit code for a new email
        logger.info("No existing record found. Generating a new verification code for email: {}", toEmail);
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
        logger.info("New verification code saved for email: {}", toEmail);

        return sendEmail(toEmail, code);
    }


        public Map<String, Object> sendEmail(String toEmail, String code) {
            String subject = "Email Verification Code";
            String currentYear = String.valueOf(LocalDate.now().getYear());  // Dynamic year

            String messageText = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; color: #333; background-color: #ffffff; text-align: center; padding: 20px; }" +
                    "h1 { color: #4CAF50; font-size: 36px; }" +
                    ".content { background-color: #f4f4f4; padding: 30px; border-radius: 10px; margin-top: 20px; }" +
                    ".code-box { display: inline-block; background-color: #4CAF50; color: white; font-weight: bold; padding: 15px 30px; font-size: 20px; margin-top: 20px; border-radius: 10px; }" +
                    ".footer { margin-top: 30px; font-size: 14px; color: #777; }" +
                    ".footer a { color: #4CAF50; text-decoration: none; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h1> Waladom - ولاضم</h1>" +  // Organization Name in Arabic and English
                    "<div class='content'>" +
                    "<h2>Verification Code</h2>" +
                    "<p>Please use this code to verify your email address.</p>" +
                    "<p>يرجى استخدام الرمز التالي للتحقق من بريدك الإلكتروني.</p>" +
                    "<p>Veuillez utiliser le code suivant pour vérifier votre adresse e-mail.</p>" +
                    "<div class='code-box'>" + code + "</div>" +
                    "<p>If you did not request this verification, please ignore this email.</p>" +
                    "<p>إذا لم تطلب هذا التحقق، يرجى تجاهل هذا البريد الإلكتروني.</p>" +
                    "<p>Si vous n'avez pas demandé cette vérification, veuillez ignorer cet e-mail.</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>For more information, contact us at: <a href='mailto:contact@waladom.org'>contact@waladom.org</a></p>" +
                    "<p>&copy; " + currentYear + " Waladom. All rights reserved.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            Map<String, Object> emailPayload = Map.of(
                    "from", "contact@waladom.org",
                    "to", toEmail,
                    "subject", subject,
                    "html", messageText
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);
            String resendApiUrl = "https://api.resend.com/emails";

            try {
                logger.info("Sending email to: {}", toEmail);
                ResponseEntity<String> response = restTemplate.exchange(
                        resendApiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                    logger.info("Verification email sent successfully to: {}", toEmail);
                    return Map.of("send", true, "message", "Verification code sent successfully", "messageNumber", 2);
                } else {
                    logger.error("Failed to send email: {}", response.getBody());
                    return Map.of("send", false, "message", "Failed to send email: " + response.getBody(), "messageNumber", 3);
                }
            } catch (Exception e) {
                logger.error("Error sending email to {}: {}", toEmail, e.getMessage(), e);
                return Map.of("send", false, "message", "Error sending email: " + e.getMessage(), "messageNumber", 4);
            }
        }


    public Map<String, Object> verifyCode(String code, String email) {
        // Find the verification code record in the database
        EmailVerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode != null) {
            Instant now = Instant.now();

            if (verificationCode.isVerified()) {
                return Map.of("verified", false, "message", "Email is already verified.", "messageNumber", 1);
            }

            if (verificationCode.getExpiresAt().isBefore(now) && verificationCode.getVerificationCode().equals(code)) {
                return Map.of("verified", false, "message", "Code has expired.", "messageNumber", 2);
            }

            if (verificationCode.getVerificationCode().equals(code)) {
                verificationCode.setVerified(true);
                verificationCodeRepository.save(verificationCode);
                return Map.of("verified", true, "message", "Email verified successfully!",  "messageNumber", 3);
            }

            return Map.of("verified", false, "message", "Invalid code.", "messageNumber", 4);
        }

        return Map.of("verified", false, "message", "No verification request found for this email.", "messageNumber", 5);
    }



        // Method to send the contact message
        public Map<String, Object> sendContactMessage(String email, String phoneNumber, String firstName, String lastName, String subject, String message) {
            String currentYear = String.valueOf(LocalDate.now().getYear());  // Dynamic year

            // HTML message body
            String messageText = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px; }" +
                    ".container { background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                    ".header { color: #4CAF50; font-size: 24px; margin-bottom: 20px; }" +
                    ".info { margin-bottom: 15px; font-size: 16px; color: #555; }" +
                    ".info span { font-weight: bold; }" +
                    ".message { font-size: 16px; color: #555; padding: 10px; border: 1px solid #ccc; border-radius: 5px; margin-top: 20px; background-color: #f4f4f4; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2 class='header'>New Contact Request</h2>" +
                    "<div class='info'><span>Name:</span> " + firstName + " " + lastName + "</div>" +
                    "<div class='info'><span>Email:</span> " + email + "</div>" +
                    "<div class='info'><span>Phone:</span> " + phoneNumber + "</div>" +
                    "<div class='info'><span>Subject:</span> " + subject + "</div>" +
                    "<div class='message'><span>Message:</span><br>" + message + "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // Email payload
            Map<String, Object> emailPayload = Map.of(
                    "from", contactEmail,
                    "to", contactEmail,  // Sending to our own email
                    "subject", subject,
                    "html", messageText
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);

            try {
                logger.info("Attempting to send contact message from: {}", email);

                // Send the email using the Resend API
                ResponseEntity<String> response = restTemplate.exchange(
                        resendApiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                    logger.info("Contact message successfully sent to: {}", contactEmail);
                    return Map.of("send", true, "message", "Your message has been sent successfully.");
                } else {
                    // Log failure with response details
                    logger.error("Failed to send contact message. Response: {}", response.getBody());
                    return Map.of("send", false, "message", "Failed to send email: " + response.getBody());
                }
            } catch (Exception e) {
                // Log error with exception details
                logger.error("Error sending contact message. Exception: {}", e.getMessage(), e);
                return Map.of("send", false, "message", "Error sending email: " + e.getMessage());
            }
        }

    public boolean isValidRequest(Map<String, String> request) {
        logger.error("Entering method isValidRequest()");

        return request.containsKey("email") && StringUtils.hasText(request.get("email")) &&
                request.containsKey("phoneNumber") && StringUtils.hasText(request.get("phoneNumber")) &&
                request.containsKey("firstName") && StringUtils.hasText(request.get("firstName")) &&
                request.containsKey("lastName") && StringUtils.hasText(request.get("lastName")) &&
                request.containsKey("subject") && StringUtils.hasText(request.get("subject")) &&
                request.containsKey("message") && StringUtils.hasText(request.get("message"));
    }


    public Map<String, Object> sendAccountValidationEmail(String toEmail) {
        String subject = "Account Validation - حساب التحقق - Validation du compte";
        String currentYear = String.valueOf(LocalDate.now().getYear());  // Dynamic year

        String messageText = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; color: #333; background-color: #ffffff; text-align: center; padding: 20px; }" +
                "h1 { color: #4CAF50; font-size: 36px; }" +
                ".content { background-color: #f4f4f4; padding: 30px; border-radius: 10px; margin-top: 20px; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #777; }" +
                ".footer a { color: #4CAF50; text-decoration: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1> Waladom - ولاضم</h1>" +  // Organization Name in Arabic and English
                "<div class='content'>" +
                "<h2>Account Successfully Validated</h2>" +
                "<p>Your account has been successfully validated, now you can login!.</p>" +
                "<p>تم التحقق من حسابك بنجاح! يمكنك تسجيل الدخول إلى حسابك.</p>" +
                "<p>Votre compte a été validé avec succès, vous pouvez vous connecter sur votre compte maintenant!.</p>" +
                "<p>If you did not initiate this action, please contact us immediately.</p>" +
                "<p>إذا لم تقم بإجراء هذه العملية، يرجى الاتصال بنا على الفور.</p>" +
                "<p>Si vous n'avez pas initié cette action, veuillez nous contacter immédiatement.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>For more information, contact us at: <a href='mailto:contact@waladom.org'>contact@waladom.org</a></p>" +
                "<p>&copy; " + currentYear + " Waladom. All rights reserved.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        Map<String, Object> emailPayload = Map.of(
                "from", "contact@waladom.org",
                "to", toEmail,
                "subject", subject,
                "html", messageText
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);
        String resendApiUrl = "https://api.resend.com/emails";

        try {
            logger.info("Sending account validation email to: {}", toEmail);
            ResponseEntity<String> response = restTemplate.exchange(
                    resendApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                logger.info("Account validation email sent successfully to: {}", toEmail);
                return Map.of("send", true, "message", "Account validation email sent successfully");
            } else {
                logger.error("Failed to send account validation email: {}", response.getBody());
                return Map.of("send", false, "message", "Failed to send account validation email: " + response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending account validation email to {}: {}", toEmail, e.getMessage(), e);
            return Map.of("send", false, "message", "Error sending account validation email: " + e.getMessage());
        }
    }

    public Map<String, Object> sendPasswordUpdateEmail(String toEmail) {
        logger.info("Preparing to send password update email to {}", toEmail);

        String subject = "Password Updated - تم تحديث كلمة المرور - Mot de passe mis à jour";
        String currentYear = String.valueOf(LocalDate.now().getYear());  // Dynamic year

        String messageText = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; color: #333; background-color: #ffffff; text-align: center; padding: 20px; }" +
                "h1 { color: #4CAF50; font-size: 36px; }" +
                ".content { background-color: #f4f4f4; padding: 30px; border-radius: 10px; margin-top: 20px; }" +
                ".alert { color: red; font-weight: bold; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #777; }" +
                ".footer a { color: #4CAF50; text-decoration: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1> Waladom - ولاضم</h1>" +  // Organization Name in Arabic and English
                "<div class='content'>" +
                "<h2>Password Successfully Updated</h2>" +
                "<p>Your account password has been successfully updated.</p>" +
                "<p>تم تحديث كلمة مرور حسابك بنجاح.</p>" +
                "<p>Le mot de passe de votre compte a été mis à jour avec succès.</p>" +
                "<p class='alert'>If you did not make this change, please contact us immediately!</p>" +
                "<p class='alert'>إذا لم تقم بإجراء هذا التغيير، يرجى الاتصال بنا على الفور!</p>" +
                "<p class='alert'>Si vous n'avez pas effectué ce changement, veuillez nous contacter immédiatement!</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>For more information, contact us at: <a href='mailto:contact@waladom.org'>contact@waladom.org</a></p>" +
                "<p>&copy; " + currentYear + " Waladom. All rights reserved.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        Map<String, Object> emailPayload = Map.of(
                "from", "contact@waladom.org",
                "to", toEmail,
                "subject", subject,
                "html", messageText
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);
        String resendApiUrl = "https://api.resend.com/emails";

        try {
            logger.debug("Sending request to email API: {}", resendApiUrl);
            logger.debug("Request payload: {}", emailPayload);

            ResponseEntity<String> response = restTemplate.exchange(
                    resendApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                logger.info("Password update email sent successfully to {}", toEmail);
                return Map.of("send", true, "message", "Password update email sent successfully");
            } else {
                logger.error("Failed to send password update email: {}", response.getBody());
                return Map.of("send", false, "message", "Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending password update email to {}: {}", toEmail, e.getMessage(), e);
            return Map.of("send", false, "message", "Error sending email: " + e.getMessage());
        }

}
    public Map<String, Object> sendRegistrationConfirmationEmail(String toEmail, String registrationRequestId) {
        logger.info("Preparing to send registration confirmation email to {}", toEmail);

        String subject = "Registration Request Received - تم استلام طلب التسجيل - Demande d'inscription reçue";
        String currentYear = String.valueOf(LocalDate.now().getYear());

        String messageText = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; color: #333; background-color: #ffffff; text-align: center; padding: 20px; }" +
                "h1 { color: #4CAF50; font-size: 36px; }" +
                ".content { background-color: #f4f4f4; padding: 30px; border-radius: 10px; margin-top: 20px; }" +
                ".footer { margin-top: 30px; font-size: 14px; color: #777; }" +
                ".footer a { color: #4CAF50; text-decoration: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1> Waladom - ولاضم</h1>" +
                "<div class='content'>" +
                "<h2>Registration Request Received</h2>" +
                "<p>Dear user,</p>" +
                "<p>We have received your registration request. Your request is under review.</p>" +
                "<p>Your Registration Request ID: <strong>" + registrationRequestId + "</strong></p>" +
                "<p>You will be notified once your request is approved.</p>" +
                "<hr>" +
                "<p>عزيزي المستخدم،</p>" +
                "<p>لقد استلمنا طلب تسجيلك، وهو الآن قيد المراجعة.</p>" +
                "<p>رقم طلب التسجيل الخاص بك: <strong>" + registrationRequestId + "</strong></p>" +
                "<p>سيتم إشعارك بمجرد الموافقة على طلبك.</p>" +
                "<hr>" +
                "<p>Cher utilisateur,</p>" +
                "<p>Nous avons bien reçu votre demande d'inscription. Votre demande est en cours d'examen.</p>" +
                "<p>ID de votre demande d'inscription : <strong>" + registrationRequestId + "</strong></p>" +
                "<p>Vous serez notifié une fois votre demande acceptée.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>For more information, contact us at: <a href='mailto:contact@waladom.org'>contact@waladom.org</a></p>" +
                "<p>&copy; " + currentYear + " Waladom. All rights reserved.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        Map<String, Object> emailPayload = Map.of(
                "from", "contact@waladom.org",
                "to", toEmail,
                "subject", subject,
                "html", messageText
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(emailPayload, headers);
        String resendApiUrl = "https://api.resend.com/emails";

        try {
            logger.debug("Sending request to email API: {}", resendApiUrl);
            logger.debug("Request payload: {}", emailPayload);

            ResponseEntity<String> response = restTemplate.exchange(
                    resendApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                logger.info("Registration confirmation email sent successfully to {}", toEmail);
                return Map.of("send", true, "message", "Registration confirmation email sent successfully");
            } else {
                logger.error("Failed to send registration confirmation email: {}", response.getBody());
                return Map.of("send", false, "message", "Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending registration confirmation email to {}: {}", toEmail, e.getMessage(), e);
            return Map.of("send", false, "message", "Error sending email: " + e.getMessage());
        }
    }



}
