package com.phanta.waladom.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Utility method to build a JSON error response.
     */
    private Map<String, Object> buildErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    /**
     * Handler for 404 (Not Found) errors.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        String path = ex.getRequestURL();// Extract the requested URL
        System.out.println(ex);
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested URL was not found on the server.",
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI(); // Get the URL causing the error
        Map<String, Object> errorResponse = new HashMap<>();

        // Check if the exception is caused by trying to update a non-existent user or element
        if (ex instanceof JpaObjectRetrievalFailureException || ex instanceof DataIntegrityViolationException) {
            // These exceptions often occur when trying to update an entity that doesn't exist in the database
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "The entity you are trying to update does not exist.");
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("path", path);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Check for database-related exceptions like InvalidDataAccessApiUsageException
        if (ex instanceof InvalidDataAccessApiUsageException) {
            // Log the error to the console (or to a logger) for debugging purposes
            ex.printStackTrace();  // You can also use a logging framework like SLF4J instead of printStackTrace

            // Set a generic error message for the client
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "An error occurred while performing actions in the Database.");
        } else {
            // Handle other exceptions normally (unexpected errors)
            errorResponse = buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred: " + ex.getMessage(),
                    path
            );
        }

        // Add timestamp and path
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("path", path);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // You can create a method to build error response in a more structured way  /**
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String path = request.getRequestURI(); // Get the URL causing the error
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Access Denied: " + ex.getMessage(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        // Build the error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", "Invalid parameter value provided. For boolean fields, only true or false are allowed. Strings like 'true' or 'false' are not accepted. " +
                "For 'latitude' and 'longitude', values must be numbers (e.g., 2433.544). For 'taxPercentage', values must be a number or double.");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(errorResponse);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        // Log the exception for debugging purposes
        ex.printStackTrace();

        // Extract column name from the exception message (e.g., "email" or "phone")
        String fieldName = extractFieldNameFromMessage(ex.getMessage());

        // Create a dynamic message based on the violated column
        String userMessage = String.format("The %s is already registered. Please use a different one.", fieldName);

        // Get the current timestamp
        LocalDateTime timestamp = LocalDateTime.now();

        // Get the request path
        String path = request.getDescription(false).replace("uri=", "");

        // Create and return the error response with timestamp and path
        ErrorResponse errorResponse = new ErrorResponse(
                "BAD_REQUEST",
                userMessage,
                HttpStatus.BAD_REQUEST.value(),
                timestamp,
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String extractFieldNameFromMessage(String message) {
        // Updated regular expression to match column names more reliably
        String pattern = "Key \\((.*?)\\)=";

        // Extract field name from the message if it matches the pattern
        String fieldName = "Unknown";  // Default value
        if (message != null && message.contains("Key (")) {
            // Try extracting the field name using the regular expression
            Matcher matcher = Pattern.compile(pattern).matcher(message);
            if (matcher.find()) {
                fieldName = matcher.group(1);  // Capture the field name inside parentheses
            }
        }

        return fieldName;
    }



}
