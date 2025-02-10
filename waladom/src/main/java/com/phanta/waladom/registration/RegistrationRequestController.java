package com.phanta.waladom.registration;

import com.phanta.waladom.config.ErrorResponse;
import com.phanta.waladom.shared.user.UserAndRegistrationService;
import com.phanta.waladom.user.UserRequestDTO;
import com.phanta.waladom.user.UserRequestValidator;
import com.phanta.waladom.user.UserResponseDTO;
import com.phanta.waladom.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.Logger;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/register")
public class RegistrationRequestController {

    private static final Logger logger = LogManager.getLogger(RegistrationRequestController.class);

    @Autowired
    private final UserAndRegistrationService userAndRegistrationService;

    @Autowired
    private final UserService userService;

    @Autowired
    public RegistrationRequestController(UserAndRegistrationService userAndRegistrationService, UserService userService) {
        this.userAndRegistrationService = userAndRegistrationService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequest) {
        logger.info("Received request to create user: " + userRequest);

        ResponseEntity<Map<String, Object>> validationResponse = UserRequestValidator.validateUserRequest(userRequest);

        if (validationResponse != null) {
            logger.warn("Validation failed for user request: " + userRequest);
            // Return bad request with validation error
            return validationResponse;
        }

        logger.info("Validation passed for user request. Proceeding with creation.");
        return userAndRegistrationService.createUserOrRegReq(userRequest, false);
    }

    @GetMapping("/get/all")
    public List<UserResponseDTO> getAllRegRequest() {
        logger.info("Received request to fetch all registration requests.");
        List<UserResponseDTO> registrations = userAndRegistrationService.getAllRegistrationRequests();
        logger.info("Fetched " + registrations.size() + " registration requests.");
        return registrations;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRegistrationRequest(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Received request to update registration with ID: " + id);

        if (userRequestDTO == null || (userService.isEmpty(userRequestDTO) && userAndRegistrationService.isVaildatedEmpty(userRequestDTO))) {
            logger.warn("Update request for ID: " + id + " contains no fields to update or all fields are empty.");
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Bad Request",
                    "No fields to update or all fields are empty",
                    HttpStatus.BAD_REQUEST.value(),
                    LocalDateTime.now(),
                    "/api/user/register/update/" + id
            ));
        }

        try {
            logger.info("Attempting to update registration request with ID: " + id);
            ResponseEntity<?> response = userAndRegistrationService.updateRegistrationRequest(id, userRequestDTO);
            logger.info("Successfully updated registration with ID: " + id);
            return response;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while updating registration with ID: " + id, e);
            throw new DataIntegrityViolationException("Unique constraint violation - Duplicate entry detected");
        } catch (RuntimeException e) {
            logger.error("Unexpected error occurred while updating registration with ID: " + id, e);
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        logger.info("Received request to delete registration with ID: " + id);

        try {
            userAndRegistrationService.deleteRegistrationRequest(id);
            logger.info("Successfully deleted registration with ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to delete registration with ID: " + id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getRegReqById(@PathVariable String id) {
        logger.info("Received request to fetch registration request with ID: " + id);

        Optional<UserResponseDTO> user = userAndRegistrationService.getRegistrationById(id);
        if (user.isEmpty()) {
            logger.warn("No registration request found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Not Found",
                            "message", "User not found",
                            "path", "/api/user/" + id
                    ));
        }

        logger.info("Successfully fetched registration request with ID: " + id);
        return ResponseEntity.ok(user.get());
    }
}
