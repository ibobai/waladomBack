package com.phanta.waladom.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validating user request data.
 */
public class UserRequestValidator {

    private static final List<Boolean> ACTIVE_CONDITIONS = Arrays.asList(true, false);
    private static final List<String> ALLOWED_ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN", "ROLE_EDITOR");
    private static final List<String> CORRECT_SEX = Arrays.asList("f", "F", "m", "M");

    /**
     * Validates the fields of a user request object.
     *
     * @param userRequest The user request object to validate.
     * @return A ResponseEntity containing the error response if validation fails, or null if validation passes.
     */
    public static ResponseEntity<Map<String, Object>> validateUserRequest(UserRequestDTO userRequest) {
        // Validate required fields
        if (userRequest.getFirstName() == null || userRequest.getFirstName().isEmpty()) {
            return buildErrorResponse("Field 'firstName' is required.");
        }
        if (userRequest.getLastName() == null || userRequest.getLastName().isEmpty()) {
            return buildErrorResponse("Field 'lastName' is required.");
        }
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            return buildErrorResponse("Field 'email' is required.");
        }
        if (userRequest.getPhone() == null || userRequest.getPhone().isEmpty()) {
            return buildErrorResponse("Field 'phone' is required.");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            return buildErrorResponse("Field 'password' is required.");
        }

        if (userRequest.getBirthDate() == null) {
            return buildErrorResponse("Field 'birthDate' is required.");
        }

        // Validate isActive field
        if (userRequest.getIsActive() == null || !ACTIVE_CONDITIONS.contains(userRequest.getIsActive())) {
            return buildErrorResponse("Field 'isActive' is required and must be a boolean (true or false).");
        }

        // Validate sex field
        if (!CORRECT_SEX.contains(userRequest.getSex())) {
            return buildErrorResponse("Field 'sex' must be one of the following: ('f', 'F', 'm', 'M'). 'M' for Masculine, 'F' for Feminine.");
        }

        // Validate role field
        if (userRequest.getRole() == null || userRequest.getRole().isEmpty() || !ALLOWED_ROLES.contains(userRequest.getRole())) {
            return buildErrorResponse("Field 'role' is required and must be one of the following: (ROLE_USER, ROLE_ADMIN, ROLE_EDITOR).");
        }

        // Validate status
        if (userRequest.getStatus() == null || userRequest.getStatus().isEmpty()) {
            return buildErrorResponse("Field 'status' is required.");
        }

        // Validate tribe
        if (userRequest.getTribe() == null || userRequest.getTribe().isEmpty()) {
            return buildErrorResponse("Field 'tribe' is required.");
        }

        // Validate current location fields
        if (userRequest.getCurrentCountry() == null || userRequest.getCurrentCountry().isEmpty()) {
            return buildErrorResponse("Field 'currentCountry' is required.");
        }
        if (userRequest.getCurrentCity() == null || userRequest.getCurrentCity().isEmpty()) {
            return buildErrorResponse("Field 'currentCity' is required.");
        }
        if (userRequest.getCurrentVillage() == null || userRequest.getCurrentVillage().isEmpty()) {
            return buildErrorResponse("Field 'currentVillage' is required.");
        }

        // Validate birth location fields
        if (userRequest.getBirthCountry() == null || userRequest.getBirthCountry().isEmpty()) {
            return buildErrorResponse("Field 'birthCountry' is required.");
        }
        if (userRequest.getBirthCity() == null || userRequest.getBirthCity().isEmpty()) {
            return buildErrorResponse("Field 'birthCity' is required.");
        }
        if (userRequest.getBirthVillage() == null || userRequest.getBirthVillage().isEmpty()) {
            return buildErrorResponse("Field 'birthVillage' is required.");
        }

        // Validate marital status
        if (userRequest.getMaritalStatus() == null || userRequest.getMaritalStatus().isEmpty()) {
            return buildErrorResponse("Field 'maritalStatus' is required.");
        }

        // Validate mothers' names
        if (userRequest.getMothersFirstName() == null || userRequest.getMothersFirstName().isEmpty()) {
            return buildErrorResponse("Field 'mothersFirstName' is required.");
        }
        if (userRequest.getMothersLastName() == null || userRequest.getMothersLastName().isEmpty()) {
            return buildErrorResponse("Field 'mothersLastName' is required.");
        }

        // Validate nationalities
        if (userRequest.getNationalities() == null || userRequest.getNationalities().isEmpty()) {
            return buildErrorResponse("Field 'nationalities' is required and must contain at least one nationality.");
        }

        // Validate photo fields
        if (userRequest.getIdProofPhotoFront() == null || userRequest.getIdProofPhotoFront().isEmpty()) {
            return buildErrorResponse("Field 'idProofPhotoFront' is required.");
        }
        if (userRequest.getIdProofPhotoBack() == null || userRequest.getIdProofPhotoBack().isEmpty()) {
            return buildErrorResponse("Field 'idProofPhotoBack' is required.");
        }
        if (userRequest.getWaladomCardPhoto() == null || userRequest.getWaladomCardPhoto().isEmpty()) {
            return buildErrorResponse("Field 'waladomCardPhoto' is required.");
        }
        if (userRequest.getOccupation() == null || userRequest.getOccupation().isEmpty()) {
            return buildErrorResponse("Field 'Occupation' is required.");
        }

        // Optional fields (number of kids, occupation, comments) do not need validation unless specific rules apply

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
    private static ResponseEntity<Map<String, Object>> buildErrorResponse(String message){
        return buildErrorResponse(message, "/api/user/createDTO");
    }
}
