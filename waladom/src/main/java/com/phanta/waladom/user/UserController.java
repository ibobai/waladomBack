package com.phanta.waladom.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phanta.waladom.shared.user.UserAndRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = " http://localhost:5173")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    private final UserAndRegistrationService userAndRegistrationService;


    private final ObjectMapper objectMapper; // Used for JSON parsing

    @Autowired
    public UserController(UserService userService, UserAndRegistrationService userAndRegistrationService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.userAndRegistrationService = userAndRegistrationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/get/all")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        Optional<UserResponseDTO> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Not Found",
                            "message", "User not found",
                            "path", "/api/user/" + id
                    ));
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }


    @PostMapping("/createDTO")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequest) {
        ResponseEntity<Map<String, Object>> validationResponse = UserRequestValidator.validateUserRequest(userRequest);

        if (validationResponse != null) {
            // Return bad request with validation error
            return validationResponse;
        }
        return userAndRegistrationService.createUserOrRegReq(userRequest, true);
       // return userService.createUserDTO(userRequest);
    }
    /**
     * Handles the PUT request to update a user.
     *
     * @param id             The ID of the user to update.
     * @param userRequestDTO The request body containing the update details.
     * @return ResponseEntity with the updated user or error details.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null || userService.isEmpty(userRequestDTO)) {
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
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
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
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Validates an email address.
     *
     * @param requestBody The object containing email details in the request body.
     * @return ResponseEntity with a JSON object containing the validation result.
     */
    @PostMapping("/email/validate")
    public ResponseEntity<?> validateEmail(@RequestBody Object requestBody) {
        try {
            Map<String, Object> requestMap = objectMapper.convertValue(requestBody, Map.class);
            String email = (String) requestMap.get("email");
            if(email == null || email.isBlank()){
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "message", "Param 'email' is required",
                                "path", "/api/user/email/validate"
                        )
                );
            }
            return ResponseEntity.ok(userService.validateEmail(email));

        } catch (Exception e){
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "500",
                            "message", "internal error",
                            "path","/api/user/email/validate",
                            "error", e.getMessage()
                    )
            );
        }
    }

    @PostMapping("/phone/validate")
    public ResponseEntity<?> validatePhone(@RequestBody Object requestBody) {
        try {
            Map<String, Object> requestMap = objectMapper.convertValue(requestBody, Map.class);
            String phone = (String) requestMap.get("phone");
            if(phone == null || phone.isBlank()){
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "message", "Param 'phone' is required",
                                "path", "/api/user/validate/phone"
                        )
                );
            }
            return ResponseEntity.ok(userService.validatePhone(phone));

        } catch (Exception e){
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "500",
                            "message", "internal error",
                            "path","/api/user/phone/validate",
                                "error", e.getMessage()
                    )
            );
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }
}
