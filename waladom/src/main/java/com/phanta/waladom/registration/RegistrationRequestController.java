package com.phanta.waladom.registration;

import com.phanta.waladom.shared.user.UserAndRegistrationService;
import com.phanta.waladom.user.UserRequestDTO;
import com.phanta.waladom.user.UserRequestValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/user")
public class RegistrationRequestController {

    private final UserAndRegistrationService userAndRegistrationService;

    public RegistrationRequestController(UserAndRegistrationService userAndRegistrationService) {
        this.userAndRegistrationService = userAndRegistrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDTO userRequest) {
        ResponseEntity<Map<String, Object>> validationResponse = UserRequestValidator.validateUserRequest(userRequest);

        if (validationResponse != null) {
            // Return bad request with validation error
            return validationResponse;
        }
        return userAndRegistrationService.createUserOrRegReq(userRequest, false);
    }
}
