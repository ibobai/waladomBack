package com.phanta.waladom;

import com.phanta.waladom.registration.RegistrationRequestRepository;
import com.phanta.waladom.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WaladomApplicationTests {

	@Autowired
	private final RegistrationRequestRepository registrationRequestRepository;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
    WaladomApplicationTests(RegistrationRequestRepository registrationRequestRepository, UserRepository userRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
        this.userRepository = userRepository;
    }

    @Test
	void contextLoads() {
	}






}
