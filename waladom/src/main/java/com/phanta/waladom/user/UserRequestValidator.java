package com.phanta.waladom.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating user request data.
 */
public class UserRequestValidator {

    private static final Logger LOGGER = Logger.getLogger(UserRequestValidator.class.getName());

    private static final List<Boolean> ACTIVE_CONDITIONS = Arrays.asList(true, false);
    private static final List<String> ALLOWED_ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN",
            "ROLE_EDITOR", "ROLE_CONTENT_MANAGER", "ROLE_MODERATOR");
    private static final List<String> CORRECT_SEX = Arrays.asList("f", "F", "m", "M");

    /**
     * Validates the fields of a user request object.
     *
     * @param userRequest The user request object to validate.
     * @return A ResponseEntity containing the error response if validation fails, or null if validation passes.
     */
    public static ResponseEntity<Map<String, Object>> validateUserRequest(UserRequestDTO userRequest) {
        LOGGER.info("Starting validation for user request.");

        // Validate required fields
        if (userRequest.getFirstName() == null || userRequest.getFirstName().isEmpty()) {
            LOGGER.warning("Validation failed: 'firstName' is missing.");
            return buildErrorResponse("Field 'firstName' is required.");
        }
        if (userRequest.getLastName() == null || userRequest.getLastName().isEmpty()) {
            LOGGER.warning("Validation failed: 'lastName' is missing.");
            return buildErrorResponse("Field 'lastName' is required.");
        }
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            LOGGER.warning("Validation failed: 'email' is missing.");
            return buildErrorResponse("Field 'email' is required.");
        }
        if (userRequest.getPhone() == null || userRequest.getPhone().isEmpty()) {
            LOGGER.warning("Validation failed: 'phone' is missing.");
            return buildErrorResponse("Field 'phone' is required.");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            LOGGER.warning("Validation failed: 'password' is missing.");
            return buildErrorResponse("Field 'password' is required.");
        }

        if (userRequest.getBirthDate() == null) {
            LOGGER.warning("Validation failed: 'birthDate' is missing.");
            return buildErrorResponse("Field 'birthDate' is required.");
        }

        if (userRequest.getConnectionMethod() == null || userRequest.getConnectionMethod().isEmpty()) {
            LOGGER.warning("Validation failed: 'connectionMethod' is missing.");
            return buildErrorResponse("Field 'connectionMethod' is required.");
        }

        // Validate isActive field
        if (userRequest.getIsActive() == null || !ACTIVE_CONDITIONS.contains(userRequest.getIsActive())) {
            LOGGER.warning("Validation failed: 'isActive' must be a boolean.");
            return buildErrorResponse("Field 'isActive' is required and must be a boolean (true or false).");
        }

        // Validate sex field
        if (!CORRECT_SEX.contains(userRequest.getSex())) {
            LOGGER.warning("Validation failed: 'sex' value is invalid.");
            return buildErrorResponse("Field 'sex' must be one of the following: ('f', 'F', 'm', 'M'). 'M' for Masculine, 'F' for Feminine.");
        }

        // Validate role field
        if (userRequest.getRole() == null || userRequest.getRole().isEmpty() || !ALLOWED_ROLES.contains(userRequest.getRole())) {
            LOGGER.warning("Validation failed: 'role' value is invalid.");
            return buildErrorResponse("Field 'role' is required and must be one of the following: (ROLE_USER, ROLE_ADMIN, ROLE_EDITOR).");
        }

        // Validate status
        if (userRequest.getStatus() == null || userRequest.getStatus().isEmpty()) {
            LOGGER.warning("Validation failed: 'status' is missing.");
            return buildErrorResponse("Field 'status' is required.");
        }

        // Validate tribe
        if (userRequest.getTribe() == null || userRequest.getTribe().isEmpty()) {
            LOGGER.warning("Validation failed: 'tribe' is missing.");
            return buildErrorResponse("Field 'tribe' is required.");
        }

        // Validate current location fields
        if (userRequest.getCurrentCountry() == null || userRequest.getCurrentCountry().isEmpty()) {
            LOGGER.warning("Validation failed: 'currentCountry' is missing.");
            return buildErrorResponse("Field 'currentCountry' is required.");
        }
        if (userRequest.getCurrentCity() == null || userRequest.getCurrentCity().isEmpty()) {
            LOGGER.warning("Validation failed: 'currentCity' is missing.");
            return buildErrorResponse("Field 'currentCity' is required.");
        }
        if (userRequest.getCurrentVillage() == null || userRequest.getCurrentVillage().isEmpty()) {
            LOGGER.warning("Validation failed: 'currentVillage' is missing.");
            return buildErrorResponse("Field 'currentVillage' is required.");
        }

        // Validate birth location fields
        if (userRequest.getBirthCountry() == null || userRequest.getBirthCountry().isEmpty()) {
            LOGGER.warning("Validation failed: 'birthCountry' is missing.");
            return buildErrorResponse("Field 'birthCountry' is required.");
        }
        if (userRequest.getBirthCity() == null || userRequest.getBirthCity().isEmpty()) {
            LOGGER.warning("Validation failed: 'birthCity' is missing.");
            return buildErrorResponse("Field 'birthCity' is required.");
        }
        if (userRequest.getBirthVillage() == null || userRequest.getBirthVillage().isEmpty()) {
            LOGGER.warning("Validation failed: 'birthVillage' is missing.");
            return buildErrorResponse("Field 'birthVillage' is required.");
        }

        // Validate marital status
        if (userRequest.getMaritalStatus() == null || userRequest.getMaritalStatus().isEmpty()) {
            LOGGER.warning("Validation failed: 'maritalStatus' is missing.");
            return buildErrorResponse("Field 'maritalStatus' is required.");
        }

        // Validate mothers' names
        if (userRequest.getMothersFirstName() == null || userRequest.getMothersFirstName().isEmpty()) {
            LOGGER.warning("Validation failed: 'mothersFirstName' is missing.");
            return buildErrorResponse("Field 'mothersFirstName' is required.");
        }
        if (userRequest.getMothersLastName() == null || userRequest.getMothersLastName().isEmpty()) {
            LOGGER.warning("Validation failed: 'mothersLastName' is missing.");
            return buildErrorResponse("Field 'mothersLastName' is required.");
        }

        // Validate nationalities
        if (userRequest.getNationalities() == null || userRequest.getNationalities().isEmpty()) {
            LOGGER.warning("Validation failed: 'nationalities' is missing or empty.");
            return buildErrorResponse("Field 'nationalities' is required and must contain at least one nationality.");
        }

        // Validate photo fields
        if (userRequest.getIdProofPhotoFront() == null || userRequest.getIdProofPhotoFront().isEmpty()) {
            LOGGER.warning("Validation failed: 'idProofPhotoFront' is missing.");
            return buildErrorResponse("Field 'idProofPhotoFront' is required.");
        }
        if (userRequest.getIdProofPhotoBack() == null || userRequest.getIdProofPhotoBack().isEmpty()) {
            LOGGER.warning("Validation failed: 'idProofPhotoBack' is missing.");
            return buildErrorResponse("Field 'idProofPhotoBack' is required.");
        }
        if (userRequest.getWaladomCardPhoto() == null || userRequest.getWaladomCardPhoto().isEmpty()) {
            LOGGER.warning("Validation failed: 'waladomCardPhoto' is missing.");
            return buildErrorResponse("Field 'waladomCardPhoto' is required.");
        }
        if (userRequest.getOccupation() == null || userRequest.getOccupation().isEmpty()) {
            LOGGER.warning("Validation failed: 'Occupation' is missing.");
            return buildErrorResponse("Field 'Occupation' is required.");
        }

        LOGGER.info("User request validation passed successfully.");
        // If all validations pass, return null
        return null;
    }

    /**
     * Builds an error response as a ResponseEntity.
     *
     * @param message The error message to include in the response.
     * @return A ResponseEntity containing the error details.
     */
    private static ResponseEntity<Map<String, Object>> buildErrorResponse(String message, String path) {
        LOGGER.log(Level.SEVERE, "Error occurred during validation: {0}", message);
        return ResponseEntity.badRequest().body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "message", message,
                        "path", path
                )
        );
    }

    private static ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        return buildErrorResponse(message, "/api/user/createDTO");
    }
}
