package com.phanta.waladom.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phanta.waladom.shared.user.UserAndRegistrationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    private final UserAndRegistrationService userAndRegistrationService;

    private final ObjectMapper objectMapper; // Used for JSON parsing

    @Autowired
    public UserController(UserService userService, UserAndRegistrationService userAndRegistrationService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.userAndRegistrationService = userAndRegistrationService;
        this.objectMapper = objectMapper;
        logger.info("UserController initialized with UserService, UserAndRegistrationService, and ObjectMapper.");
    }

    @GetMapping("/get/all")
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Fetching all users.");
        List<UserResponseDTO> users = userService.getAllUsers();
        logger.debug("Retrieved {} users.", users.size());
        return users;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        logger.info("Fetching user with ID: {}", id);
        Optional<UserResponseDTO> user = userService.getUserById(id);
        if (user.isEmpty()) {
            logger.warn("User with ID {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Not Found",
                            "message", "User not found",
                            "path", "/api/user/" + id
                    ));
        }
        logger.debug("User found: {}", user.get());
        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Creating new user: {}", user);
        ResponseEntity<?> response = userService.createUser(user);
        logger.debug("User creation response: {}", response);
        return response;
    }

    @PostMapping("/createDTO")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequest) {
        logger.info("Creating new user with DTO: {}", userRequest);
        ResponseEntity<Map<String, Object>> validationResponse = UserRequestValidator.validateUserRequest(userRequest);

        if (validationResponse != null) {
            logger.warn("Validation failed for user request: {}", validationResponse.getBody());
            return validationResponse;
        }
        ResponseEntity<?> response = userAndRegistrationService.createUserOrRegReq(userRequest, true);
        logger.debug("User creation response: {}", response);
        return response;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Updating user with ID: {}", id);
        if (userRequestDTO == null || userService.isEmpty(userRequestDTO)) {
            logger.warn("Invalid update request for user ID {}: No fields to update.", id);
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", "No fields to update or all fields are empty",
                    "path", "/api/user/update/" + id
            ));
        }

        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
            logger.debug("User updated successfully: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.NOT_FOUND.value(),
                    "error", "Not Found",
                    "message", e.getMessage(),
                    "path", "/api/user/update/" + id
            ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.debug("User with ID {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/email/validate")
    public ResponseEntity<?> validateEmail(@RequestBody Object requestBody) {
        logger.info("Validating email address.");
        try {
            Map<String, Object> requestMap = objectMapper.convertValue(requestBody, Map.class);
            String email = (String) requestMap.get("email");
            if (email == null || email.isBlank()) {
                logger.warn("Email validation failed: 'email' parameter is required.");
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "message", "Param 'email' is required",
                                "path", "/api/user/email/validate"
                        )
                );
            }
            ResponseEntity<?> response = ResponseEntity.ok(userService.validateEmail(email));
            logger.debug("Email validation response: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("Error validating email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "500",
                            "message", "internal error",
                            "path", "/api/user/email/validate",
                            "error", e.getMessage()
                    )
            );
        }
    }

    @PostMapping("/phone/validate")
    public ResponseEntity<?> validatePhone(@RequestBody Object requestBody) {
        logger.info("Validating phone number.");
        try {
            Map<String, Object> requestMap = objectMapper.convertValue(requestBody, Map.class);
            String phone = (String) requestMap.get("phone");
            if (phone == null || phone.isBlank()) {
                logger.warn("Phone validation failed: 'phone' parameter is required.");
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "message", "Param 'phone' is required",
                                "path", "/api/user/validate/phone"
                        )
                );
            }
            ResponseEntity<?> response = ResponseEntity.ok(userService.validatePhone(phone));
            logger.debug("Phone validation response: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("Error validating phone: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "500",
                            "message", "internal error",
                            "path", "/api/user/phone/validate",
                            "error", e.getMessage()
                    )
            );
        }
    }

    @PostMapping("/get/by5")
    public  void getUserByFirstNameOrlastNameOrIdOrEmailOrPhone(){

    }
}