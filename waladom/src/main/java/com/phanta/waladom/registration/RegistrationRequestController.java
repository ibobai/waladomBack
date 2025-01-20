package com.phanta.waladom.registration;

import com.phanta.waladom.config.ErrorResponse;
import com.phanta.waladom.shared.user.UserAndRegistrationService;
import com.phanta.waladom.user.UserRequestDTO;
import com.phanta.waladom.user.UserRequestValidator;
import com.phanta.waladom.user.UserResponseDTO;
import com.phanta.waladom.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/register")
public class RegistrationRequestController {

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
        ResponseEntity<Map<String, Object>> validationResponse = UserRequestValidator.validateUserRequest(userRequest);

        if (validationResponse != null) {
            // Return bad request with validation error
            return validationResponse;
        }
        return userAndRegistrationService.createUserOrRegReq(userRequest, false);
    }

    @GetMapping("/get/all")
    public List<UserResponseDTO> getAllRegRequest() {
        return userAndRegistrationService.getAllRegistrationRequests();
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?>  updateRegistrationRequest(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null || (userService.isEmpty(userRequestDTO) && userAndRegistrationService.isVaildatedEmpty(userRequestDTO))) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Bad Request",
                    "No fields to update or all fields are empty",
                    HttpStatus.BAD_REQUEST.value(),
                    LocalDateTime.now(),
                    "/api/user/registration/update/" + id
            ));
        }

        try {
            return userAndRegistrationService.updateRegistrationRequest(id, userRequestDTO);

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Unique constraint violation - Duplicate entry detected");
        } catch (RuntimeException e) {
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userAndRegistrationService.deleteRegistrationRequest(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getRegReqById(@PathVariable String id) {
        Optional<UserResponseDTO> user = userAndRegistrationService.getRegistrationById(id);
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
}
