package com.phanta.waladom.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idPhoto.WaladomPhotoDTO;
import com.phanta.waladom.idPhoto.WaladomPhotoRepository;
import com.phanta.waladom.idProof.IdPhotoProofRepository;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.idProof.IdProofPhotoDTO;
import com.phanta.waladom.oauthToken.JwtTokenUtil;
import com.phanta.waladom.role.Role;
import com.phanta.waladom.role.RoleDTO;
import com.phanta.waladom.role.RoleRepository;
import com.phanta.waladom.shared.user.UserAndRegistrationService;
import com.phanta.waladom.shared.user.UserManagementService;
import com.phanta.waladom.utiles.UtilesMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // Used for JSON parsing
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private final IdPhotoProofRepository idPhotoProofRepository;
    @Autowired
    private final WaladomPhotoRepository waladomPhotoRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final UserManagementService userManagementService;
    @Autowired
    private final UserAndRegistrationService userAndRegistrationService;


    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, JwtTokenUtil jwtTokenUtil, IdPhotoProofRepository idPhotoProofRepository, WaladomPhotoRepository waladomPhotoRepository, RoleRepository roleRepository, UserManagementService userManagementService, UserAndRegistrationService userAndRegistrationService) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.idPhotoProofRepository = idPhotoProofRepository;
        this.waladomPhotoRepository = waladomPhotoRepository;
        this.roleRepository = roleRepository;
        this.userManagementService = userManagementService;
        this.userAndRegistrationService = userAndRegistrationService;
    }


    public ResponseEntity<?> createUser(User user) {
        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            // Return a bad request response with the existing user's ID
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "User already exists",
                            "userId", "USR_" + existingUser.get().getId()
                    )
            );
        }

        // Save the new user
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @Transactional
    public Optional<UserResponseDTO> getUserById(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();

                    // Map basic user details
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setPhone(user.getPhone());
                    dto.setActive(user.getActive());
                    dto.setStatus(user.getStatus());
                    dto.setTribe(user.getTribe());
                    dto.setCurrentCountry(user.getCurrentCountry());
                    dto.setCurrentCity(user.getCurrentCity());
                    dto.setCurrentVillage(user.getCurrentVillage());
                    dto.setBirthDate(user.getBirthDate());
                    dto.setBirthCountry(user.getBirthCountry());
                    dto.setBirthCity(user.getBirthCity());
                    dto.setBirthVillage(user.getBirthVillage());
                    dto.setMaritalStatus(user.getMaritalStatus());
                    dto.setNumberOfKids(user.getNumberOfKids());
                    dto.setOccupation(user.getOccupation());
                    dto.setSex(user.getSex());
                    dto.setMothersFirstName(user.getMothersFirstName());
                    dto.setMothersLastName(user.getMothersLastName());
                    dto.setNationalities(user.getNationalities());
                    dto.setComments(user.getComments());
                    dto.setConnectionMethod(user.getConnectionMethod());

                    // Map WaladomCardPhoto
                    WaladomIdPhoto waladomCard = user.getWaladomIdPhoto();
                    if (waladomCard != null) {
                        WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
                        waladomCardPhotoDTO.setId(waladomCard.getId());
                        waladomCardPhotoDTO.setPhotoUrl(waladomCard.getPhotoUrl());
                        waladomCardPhotoDTO.setCreatedAt(waladomCard.getCreatedAt());
                        waladomCardPhotoDTO.setUpdatedAt(waladomCard.getUpdatedAt());
                        dto.setWaladomCardPhoto(waladomCardPhotoDTO);
                    }

                    // Map IdProofPhotos
                    List<IdProofPhotoDTO> idProofPhotoDTOs = user.getIdProofPhotos().stream()
                            .map(idProof -> {
                                IdProofPhotoDTO proofDTO = new IdProofPhotoDTO();
                                proofDTO.setId(idProof.getId());
                                proofDTO.setPhotoUrl(idProof.getPhotoUrl());
                                proofDTO.setPhotoType(idProof.getPhotoType());
                                proofDTO.setCreatedAt(idProof.getCreatedAt());
                                proofDTO.setUpdatedAt(idProof.getUpdatedAt());
                                return proofDTO;
                            }).collect(Collectors.toList());
                    dto.setIdProofPhotos(idProofPhotoDTOs);

                    // Map Role
                    Role role = user.getRole(); // Assuming the user has a "role" field
                    if (role != null) {
                        RoleDTO roleDTO = new RoleDTO();
                        roleDTO.setId(role.getId());
                        roleDTO.setName(role.getName());
                        roleDTO.setDescription(role.getDescription());
                        roleDTO.setColor(role.getColor());
                        roleDTO.setCreatedAt(role.getCreatedAt());
                        roleDTO.setUpdatedAt(role.getUpdatedAt());
                        dto.setRole(roleDTO);
                    }

                    return dto;
                });
    }


    @Transactional
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();

                    // Map basic user details
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setPhone(user.getPhone());
                    dto.setActive(user.getActive());
                    dto.setStatus(user.getStatus());
                    dto.setTribe(user.getTribe());
                    dto.setCurrentCountry(user.getCurrentCountry());
                    dto.setCurrentCity(user.getCurrentCity());
                    dto.setCurrentVillage(user.getCurrentVillage());
                    dto.setBirthDate(user.getBirthDate());
                    dto.setBirthCountry(user.getBirthCountry());
                    dto.setBirthCity(user.getBirthCity());
                    dto.setBirthVillage(user.getBirthVillage());
                    dto.setMaritalStatus(user.getMaritalStatus());
                    dto.setNumberOfKids(user.getNumberOfKids());
                    dto.setOccupation(user.getOccupation());
                    dto.setSex(user.getSex());
                    dto.setMothersFirstName(user.getMothersFirstName());
                    dto.setMothersLastName(user.getMothersLastName());
                    dto.setNationalities(user.getNationalities());
                    dto.setComments(user.getComments());
                    dto.setCreatedAt(user.getCreatedAt());
                    dto.setUpdatedAt(user.getUpdatedAt());
                    dto.setConnectionMethod(user.getConnectionMethod());

                    // Map WaladomCardPhoto
                    WaladomIdPhoto waladomCard = user.getWaladomIdPhoto();
                    if (waladomCard != null) {
                        WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
                        waladomCardPhotoDTO.setId(waladomCard.getId());
                        waladomCardPhotoDTO.setPhotoUrl(waladomCard.getPhotoUrl());
                        waladomCardPhotoDTO.setCreatedAt(waladomCard.getCreatedAt());
                        waladomCardPhotoDTO.setUpdatedAt(waladomCard.getUpdatedAt());
                        dto.setWaladomCardPhoto(waladomCardPhotoDTO);
                    }

                    // Map IdProofPhotos
                    List<IdProofPhotoDTO> idProofPhotoDTOs = user.getIdProofPhotos().stream()
                            .map(idProof -> {
                                IdProofPhotoDTO proofDTO = new IdProofPhotoDTO();
                                proofDTO.setId(idProof.getId());
                                proofDTO.setPhotoUrl(idProof.getPhotoUrl());
                                proofDTO.setPhotoType(idProof.getPhotoType());
                                proofDTO.setCreatedAt(idProof.getCreatedAt());
                                proofDTO.setUpdatedAt(idProof.getUpdatedAt());
                                return proofDTO;
                            }).collect(Collectors.toList());
                    dto.setIdProofPhotos(idProofPhotoDTOs);

                    // Map Role
                    Role role = user.getRole();
                    if (role != null) {
                        RoleDTO roleDTO = new RoleDTO();
                        roleDTO.setId(role.getId());
                        roleDTO.setName(role.getName());
                        roleDTO.setDescription(role.getDescription());
                        roleDTO.setColor(role.getColor());
                        roleDTO.setCreatedAt(role.getCreatedAt());
                        roleDTO.setUpdatedAt(role.getUpdatedAt());
                        dto.setRole(roleDTO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }



    @Transactional
    public ResponseEntity<?> createUserDTO(UserRequestDTO userRequest) {
        try{
            // Check if the email already exists
            Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
            if (existingUser.isPresent()) {
                // Return a bad request response with the existing user's ID
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.BAD_REQUEST.value(),
                                "error", "User already exists",
                                "message", "User already exists with ID: " + existingUser.get().getId(),
                                "path", "/api/user/createDTO"
                        )
                );
            }


            // Map the personal info from the DTO to the User object
            return ResponseEntity.ok(UserResponseDTO.mapToUserResponseDTO((User) userAndRegistrationService.mapAndSaveUserWithAllDetails(userRequest, true)));


        } catch (Exception ex) {
            // Return a bad request response with the error message
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Unable to create user",
                            "message", ex.getMessage(),
                            "path", "/api/user/createDTO"
                    )
            );
        }

    }


    /**
     * Updates an existing user based on the provided ID and UserRequestDTO.
     *
     * @param id             The ID of the user to update.
     * @param userRequestDTO The request DTO containing the fields to update.
     * @return The updated user in the form of a UserResponseDTO.
     * @throws RuntimeException If the user with the given ID is not found.
     */
    public UserResponseDTO updateUser(String id, UserRequestDTO userRequestDTO) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        User existingUser = existingUserOpt.get();

        // Update fields only if they are not null or empty in the DTO
        if (userRequestDTO.getFirstName() != null && !userRequestDTO.getFirstName().isBlank()) {
            existingUser.setFirstName(userRequestDTO.getFirstName());
        }
        if (userRequestDTO.getLastName() != null && !userRequestDTO.getLastName().isBlank()) {
            existingUser.setLastName(userRequestDTO.getLastName());
        }
        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().isBlank()
                && !userRequestDTO.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            existingUser.setEmail(userRequestDTO.getEmail());
        }

        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            existingUser.setPassword(userRequestDTO.getPassword());
        }
        if (userRequestDTO.getPhone() != null && !userRequestDTO.getPhone().isBlank()) {
            existingUser.setPhone(userRequestDTO.getPhone());
        }
        if (userRequestDTO.getIsActive() != null) {
            existingUser.setActive(userRequestDTO.getIsActive());
        }
        if (userRequestDTO.getStatus() != null && !userRequestDTO.getStatus().isBlank()) {
            existingUser.setStatus(userRequestDTO.getStatus());
        }
        if (userRequestDTO.getTribe() != null && !userRequestDTO.getTribe().isBlank()) {
            existingUser.setTribe(userRequestDTO.getTribe());
        }
        if (userRequestDTO.getCurrentCountry() != null && !userRequestDTO.getCurrentCountry().isBlank()) {
            existingUser.setCurrentCountry(userRequestDTO.getCurrentCountry());
        }
        if (userRequestDTO.getCurrentCity() != null && !userRequestDTO.getCurrentCity().isBlank()) {
            existingUser.setCurrentCity(userRequestDTO.getCurrentCity());
        }
        if (userRequestDTO.getCurrentVillage() != null && !userRequestDTO.getCurrentVillage().isBlank()) {
            existingUser.setCurrentVillage(userRequestDTO.getCurrentVillage());
        }
        if (userRequestDTO.getBirthDate() != null) {
            existingUser.setBirthDate(userRequestDTO.getBirthDate());
        }
        if (userRequestDTO.getBirthCountry() != null && !userRequestDTO.getBirthCountry().isBlank()) {
            existingUser.setBirthCountry(userRequestDTO.getBirthCountry());
        }
        if (userRequestDTO.getBirthCity() != null && !userRequestDTO.getBirthCity().isBlank()) {
            existingUser.setBirthCity(userRequestDTO.getBirthCity());
        }
        if (userRequestDTO.getBirthVillage() != null && !userRequestDTO.getBirthVillage().isBlank()) {
            existingUser.setBirthVillage(userRequestDTO.getBirthVillage());
        }
        if (userRequestDTO.getMaritalStatus() != null && !userRequestDTO.getMaritalStatus().isBlank()) {
            existingUser.setMaritalStatus(userRequestDTO.getMaritalStatus());
        }
        if (userRequestDTO.getNumberOfKids() != null) {
            existingUser.setNumberOfKids(userRequestDTO.getNumberOfKids());
        }
        if (userRequestDTO.getOccupation() != null && !userRequestDTO.getOccupation().isBlank()) {
            existingUser.setOccupation(userRequestDTO.getOccupation());
        }
        if (userRequestDTO.getSex() != null && !userRequestDTO.getSex().isBlank()) {
            existingUser.setSex(userRequestDTO.getSex());
        }
        if (userRequestDTO.getMothersFirstName() != null && !userRequestDTO.getMothersFirstName().isBlank()) {
            existingUser.setMothersFirstName(userRequestDTO.getMothersFirstName());
        }
        if (userRequestDTO.getMothersLastName() != null && !userRequestDTO.getMothersLastName().isBlank()) {
            existingUser.setMothersLastName(userRequestDTO.getMothersLastName());
        }
        if (userRequestDTO.getNationalities() != null && !userRequestDTO.getNationalities().isEmpty()) {
            existingUser.setNationalities(userRequestDTO.getNationalities());
        }
        if (userRequestDTO.getComments() != null && !userRequestDTO.getComments().isBlank()) {
            existingUser.setComments(userRequestDTO.getComments());
        }
        if (userRequestDTO.getConnectionMethod() != null && !userRequestDTO.getConnectionMethod().isBlank()) {
            existingUser.setCurrentCity(userRequestDTO.getConnectionMethod());
        }

        if (userRequestDTO.getRole() != null && !userRequestDTO.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequestDTO.getRole())) {

            // Fetch role by ID
            Optional<Role> optionalRole = roleRepository.findById(userRequestDTO.getRole());

            // Handle the Optional
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + userRequestDTO.getRole() + " not found"));

            existingUser.setRole(role);
        }

        if (userRequestDTO.getIdProofPhotoFront() != null && !userRequestDTO.getIdProofPhotoFront().isBlank()) {
            IdProofPhoto idPhotoFront = idPhotoProofRepository.findByUserAndPhotoType(existingUser, "front")
                    .orElse(new IdProofPhoto());
            idPhotoFront.setUser(existingUser);
            idPhotoFront.setPhotoUrl(userRequestDTO.getIdProofPhotoFront());
            idPhotoFront.setPhotoType("front");
            idPhotoFront.setUpdatedAt(LocalDateTime.now());
            idPhotoProofRepository.save(idPhotoFront);
            existingUser.getIdProofPhotos().add(idPhotoFront); // Add directly to the existing collection
        }

        if (userRequestDTO.getIdProofPhotoBack() != null && !userRequestDTO.getIdProofPhotoBack().isBlank()) {
            IdProofPhoto idPhotoBack = idPhotoProofRepository.findByUserAndPhotoType(existingUser, "back")
                    .orElse(new IdProofPhoto());
            idPhotoBack.setUser(existingUser);
            idPhotoBack.setPhotoUrl(userRequestDTO.getIdProofPhotoBack());
            idPhotoBack.setPhotoType("back");
            idPhotoBack.setUpdatedAt(LocalDateTime.now());
            idPhotoProofRepository.save(idPhotoBack);
            existingUser.getIdProofPhotos().add(idPhotoBack); // Add directly to the existing collection
        }


        if (userRequestDTO.getWaladomCardPhoto() != null && !userRequestDTO.getWaladomCardPhoto().isBlank()) {
            WaladomIdPhoto waladomCardPhoto = waladomPhotoRepository.findByUser(existingUser)
                    .orElse(new WaladomIdPhoto());
            waladomCardPhoto.setUser(existingUser);
            waladomCardPhoto.setPhotoUrl(userRequestDTO.getWaladomCardPhoto());
            waladomCardPhoto.setUpdatedAt(LocalDateTime.now());
            waladomPhotoRepository.save(waladomCardPhoto);
            existingUser.setWaladomIdPhoto(waladomCardPhoto);


        }

        // Save and return the updated user
        //User savedUser = userRepository.save(existingUser);
        User savedUser = userManagementService.save(existingUser, userRepository);

        return UserResponseDTO.mapToUserResponseDTO(savedUser);
    }

    /**
     * Checks if a UserRequestDTO is empty (all fields are null or blank).
     *
     * @param requestBody The request DTO to check.
     * @return True if the DTO is empty, false otherwise.
     */
    public boolean isEmpty(UserRequestDTO requestBody) {
        return (requestBody.getFirstName() == null || requestBody.getFirstName().isBlank())
                && (requestBody.getLastName() == null || requestBody.getLastName().isBlank())
                && (requestBody.getEmail() == null || requestBody.getEmail().isBlank())
                && (requestBody.getPassword() == null || requestBody.getPassword().isBlank())
                && (requestBody.getPhone() == null || requestBody.getPhone().isBlank())
                && (requestBody.getIsActive() == null)
                && (requestBody.getStatus() == null || requestBody.getStatus().isBlank())
                && (requestBody.getTribe() == null || requestBody.getTribe().isBlank())
                && (requestBody.getCurrentCountry() == null || requestBody.getCurrentCountry().isBlank())
                && (requestBody.getCurrentCity() == null || requestBody.getCurrentCity().isBlank())
                && (requestBody.getCurrentVillage() == null || requestBody.getCurrentVillage().isBlank())
                && (requestBody.getBirthDate() == null)
                && (requestBody.getBirthCountry() == null || requestBody.getBirthCountry().isBlank())
                && (requestBody.getBirthCity() == null || requestBody.getBirthCity().isBlank())
                && (requestBody.getBirthVillage() == null || requestBody.getBirthVillage().isBlank())
                && (requestBody.getMaritalStatus() == null || requestBody.getMaritalStatus().isBlank())
                && (requestBody.getNumberOfKids() == null)
                && (requestBody.getOccupation() == null || requestBody.getOccupation().isBlank())
                && (requestBody.getSex() == null || requestBody.getSex().isBlank())
                && (requestBody.getMothersFirstName() == null || requestBody.getMothersFirstName().isBlank())
                && (requestBody.getMothersLastName() == null || requestBody.getMothersLastName().isBlank())
                && (requestBody.getNationalities() == null || requestBody.getNationalities().isEmpty())
                && (requestBody.getIdProofPhotoFront() == null || requestBody.getIdProofPhotoFront().isBlank())
                && (requestBody.getIdProofPhotoBack() == null || requestBody.getIdProofPhotoBack().isBlank())
                && (requestBody.getWaladomCardPhoto() == null || requestBody.getWaladomCardPhoto().isBlank())
                && (requestBody.getComments() == null || requestBody.getComments().isBlank())
                && (requestBody.getConnectionMethod() == null || requestBody.getConnectionMethod().isBlank())
                && (requestBody.getRole() == null || requestBody.getRole().isBlank());

    }



    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }


    /**
     * Validates an email address extracted from the request body.
     *
     * @param email The object containing email details.
     * @return A Map with the validation result.
     */
    public Object validateEmail(String email) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // Return a bad request response with the existing user's ID
            return
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "User already exists",
                            "message", "The email is already taken",
                            "path", "/api/user/email/validate",
                            "vaild", false

            );
        }
        Map<String, Object> response = new HashMap<>();
        try {
            // Convert the request body to a map

            // Perform basic email validation
            if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
                response.put("valid", false); // Invalid email format
                response.put("message", "Invalid email format");

                return response;
            }

            // Check if the email already exists in the database
            boolean isValid = userRepository.findByEmail(email).isEmpty();
            response.put("valid", isValid);
            response.put("message", isValid ? "Email is valid" : "Email already exists");

            return response;
        } catch (Exception e) {
            // Log the exception and return a validation failure
            e.printStackTrace();
            response.put("valid", false);
            response.put("message", "Error validating email: " + e.getMessage());

            return response;
        }
    }

    /**
     * Authenticates the user and returns the access token and refresh token.
     *
     * @param identifier    The identifier of the user extracted from Basic Auth
     * @param password The password of the user extracted from Basic Auth
     * @return ResponseEntity containing the validation result and tokens
     */
    public ResponseEntity<Map<String, Object>> login(String identifier, String password, String connectionMethod) {
        Optional<User> userOptional;

        if ("email".equalsIgnoreCase(connectionMethod)) {
            userOptional = userRepository.findByEmail(identifier);
        } else if ("phone".equalsIgnoreCase(connectionMethod)) {
            userOptional = userRepository.findByPhone(identifier);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid connection method");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> response = new HashMap<>();

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if the user meets the required conditions
            if (!password.equals(user.getPassword())) {
                response.put("valid", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!connectionMethod.equalsIgnoreCase(user.getConnectionMethod())) {
                response.put("valid", false);
                response.put("message", "Invalid connection method for this user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
                // Additional checks for login
                if (!"active".equalsIgnoreCase(user.getStatus())) {
                    response.put("valid", false);
                    response.put("message", "User account is not active");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }

                if (!user.getActive()) {
                    response.put("valid", false);
                    response.put("message", "User is not active");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }





            // Generate tokens upon successful login
            String accessToken = jwtTokenUtil.generateAccessToken(user.getEmail(), user.getRole().getName());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getEmail());

            response.put("valid", true);
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    public Object validatePhone(String phone) {

        Optional<User> existingUser = userRepository.findByPhone(phone);
        if (existingUser.isPresent()) {
            // Return a bad request response with the existing user's ID
            return Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "User already exists",
                    "message", "The phone number is already taken",
                    "path", "/api/user/phone/validate",
                    "valid", false
            );
        }

        Map<String, Object> response = new HashMap<>();
        try {
            // Extract the phone field
            // Perform basic phone number validation (e.g., E.164 format)
            if (phone == null || !phone.matches("^\\+?[1-9]\\d{1,14}$")) {
                response.put("valid", false); // Invalid phone number format
                response.put("message", "Invalid phone number format");
                return response;
            }

            // Check if the phone already exists in the database
            boolean isValid = userRepository.findByPhone(phone).isEmpty();
            response.put("valid", isValid);
            response.put("message", isValid ? "Phone number is valid" : "Phone number already exists");
            return response;
        } catch (Exception e) {
            // Log the exception and return a validation failure
            e.printStackTrace();
            response.put("valid", false);
            response.put("message", "Error validating phone number: " + e.getMessage());
            return response;
        }
    }


    public User mapAndSaveUserWithAllDetails(UserRequestDTO userRequest) {
        // Save User
        User savedUser = saveUser(userRequest);

        // Save Waladom ID Photo
        if (userRequest.getWaladomCardPhoto() != null && !userRequest.getWaladomCardPhoto().isBlank()) {
            saveWaladomIdPhoto(savedUser, userRequest.getWaladomCardPhoto());
        }

        ArrayList<IdProofPhoto> idProofPhotos = new ArrayList<>();

        // Save ID Proof Photos
        if (userRequest.getIdProofPhotoFront() != null && !userRequest.getIdProofPhotoFront().isBlank()) {

            idProofPhotos.add( saveIdProofPhoto(savedUser, userRequest.getIdProofPhotoFront(), "front"));
        }
        if (userRequest.getIdProofPhotoBack() != null && !userRequest.getIdProofPhotoBack().isBlank()) {
            idProofPhotos.add( saveIdProofPhoto(savedUser, userRequest.getIdProofPhotoFront(), "back"));
        }
        savedUser.setIdProofPhotos(idProofPhotos);
        // Return the response DTO
        return savedUser;
    }

    private User saveUser(UserRequestDTO userRequest) {
        User newUser = new User();
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(userRequest.getPassword()); // Ensure hashing is done here if needed
        newUser.setPhone(userRequest.getPhone());
        newUser.setActive(userRequest.getIsActive());
        newUser.setStatus(userRequest.getStatus());
        newUser.setTribe(userRequest.getTribe());
        newUser.setCurrentCountry(userRequest.getCurrentCountry());
        newUser.setCurrentCity(userRequest.getCurrentCity());
        newUser.setCurrentVillage(userRequest.getCurrentVillage());
        newUser.setBirthDate(userRequest.getBirthDate());
        newUser.setBirthCountry(userRequest.getBirthCountry());
        newUser.setBirthCity(userRequest.getBirthCity());
        newUser.setBirthVillage(userRequest.getBirthVillage());
        newUser.setMaritalStatus(userRequest.getMaritalStatus());
        newUser.setNumberOfKids(userRequest.getNumberOfKids());
        newUser.setOccupation(userRequest.getOccupation());
        newUser.setSex(userRequest.getSex());
        newUser.setMothersFirstName(userRequest.getMothersFirstName());
        newUser.setMothersLastName(userRequest.getMothersLastName());
        newUser.setNationalities(userRequest.getNationalities());
        newUser.setComments(userRequest.getComments());
        newUser.setConnectionMethod(userRequest.getConnectionMethod());

        if (userRequest.getRole() != null && !userRequest.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequest.getRole())) {

            // Fetch role by ID
            Optional<Role> optionalRole = roleRepository.findById(userRequest.getRole());

            // Handle the Optional
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + userRequest.getRole() + " not found"));

            newUser.setRole(role);
        }

       // return userRepository.save(newUser);
        return userManagementService.save(newUser, userRepository);
    }

    private void saveWaladomIdPhoto(User user, String photoUrl) {
        WaladomIdPhoto waladomIdPhoto = new WaladomIdPhoto();
        waladomIdPhoto.setUser(user);
        waladomIdPhoto.setPhotoUrl(photoUrl);
        waladomIdPhoto.setCreatedAt(LocalDateTime.now());
        waladomIdPhoto.setUpdatedAt(LocalDateTime.now());
        waladomPhotoRepository.save(waladomIdPhoto);
        user.setWaladomIdPhoto(waladomIdPhoto);
    }


    private IdProofPhoto saveIdProofPhoto(User user, String photoUrl, String photoType) {
        IdProofPhoto idProofPhoto = new IdProofPhoto();
        idProofPhoto.setUser(user);
        idProofPhoto.setPhotoUrl(photoUrl);
        idProofPhoto.setPhotoType(photoType);
        idProofPhoto.setCreatedAt(LocalDateTime.now());
        idProofPhoto.setUpdatedAt(LocalDateTime.now());
        idPhotoProofRepository.save(idProofPhoto);
        return idProofPhoto;
    }


}