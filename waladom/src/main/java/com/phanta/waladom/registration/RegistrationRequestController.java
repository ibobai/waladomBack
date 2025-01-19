package com.phanta.waladom.registration;

import com.phanta.waladom.shared.user.UserAndRegistrationService;
import com.phanta.waladom.user.UserRequestDTO;
import com.phanta.waladom.user.UserRequestValidator;
import com.phanta.waladom.user.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/user/register")
public class RegistrationRequestController {

    @Autowired
    private final UserAndRegistrationService userAndRegistrationService;

    @Autowired
    public RegistrationRequestController(UserAndRegistrationService userAndRegistrationService) {
        this.userAndRegistrationService = userAndRegistrationService;
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
    public List<UserResponseDTO> getAllUsers() {
        return userAndRegistrationService.getAllRegistrationRequests();
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
