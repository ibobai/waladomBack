package com.phanta.waladom.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phanta.waladom.fileUpload.S3Service;
import com.phanta.waladom.forgotPassword.PasswordService;
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
import com.phanta.waladom.verification.email.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(UserService.class);

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
    private final EmailService   emailService;
    @Autowired
    private final PasswordService passwordService;


    @Autowired
    private final S3Service s3Service;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, JwtTokenUtil jwtTokenUtil, IdPhotoProofRepository idPhotoProofRepository, WaladomPhotoRepository waladomPhotoRepository, RoleRepository roleRepository, UserManagementService userManagementService, UserAndRegistrationService userAndRegistrationService, EmailService emailService, PasswordService passwordService, S3Service s3Service) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.idPhotoProofRepository = idPhotoProofRepository;
        this.waladomPhotoRepository = waladomPhotoRepository;
        this.roleRepository = roleRepository;
        this.userManagementService = userManagementService;
        this.userAndRegistrationService = userAndRegistrationService;
        this.emailService = emailService;
        this.passwordService = passwordService;
        this.s3Service = s3Service;
    }


    public ResponseEntity<?> createUser(User user) {
        logger.info("Attempting to create a new user with email: {}", user.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("User with email {} already exists. Returning existing user ID: {}", user.getEmail(), existingUser.get().getId());
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "User already exists",
                            "userId", "USR_" + existingUser.get().getId()
                    )
            );
        }

        User savedUser = userRepository.save(user);
        logger.info("Successfully created user with ID: {}", savedUser.getId());
        return ResponseEntity.ok(savedUser);
    }

    @Transactional
    public Optional<UserResponseDTO> getUserById(String id) {
        logger.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    logger.debug("Mapping user data for user ID: {}", id);
                    UserResponseDTO dto = new UserResponseDTO();

                    // Map basic user details
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail().toLowerCase());
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

                    dto.setApproverComment(user.getApproverComment());
                    dto.setRecommendedBy(user.getRecommendedBy());

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
        logger.info("Fetching all users from the database.");

        List<User> users = userRepository.findAllWithAssociations();

        logger.debug("Found {} users in the database.", users.size());

        return users.stream()
                .map(user -> {
                    logger.debug("Mapping user data for user ID: {}", user.getId());
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

                    dto.setRecommendedBy(user.getRecommendedBy());
                    dto.setApproverComment(user.getApproverComment());

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

        logger.info("Starting updateUser method for user ID: {}", id);

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            logger.error("User not found with ID: {}", id);
            throw new RuntimeException("User not found with ID: " + id);
        }

        Boolean passwordUpdate = false;

        User existingUser = existingUserOpt.get();
        logger.info("User found. Proceeding with updates for user ID: {}", id);

        // Update fields only if they are not null or empty in the DTO
        if (userRequestDTO.getFirstName() != null && !userRequestDTO.getFirstName().isBlank()) {
            existingUser.setFirstName(userRequestDTO.getFirstName());
            logger.debug("Updated firstName to: {}", userRequestDTO.getFirstName());
        }
        if (userRequestDTO.getLastName() != null && !userRequestDTO.getLastName().isBlank()) {
            existingUser.setLastName(userRequestDTO.getLastName());
            logger.debug("Updated lastName to: {}", userRequestDTO.getLastName());
        }
        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().isBlank()
                && !userRequestDTO.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            existingUser.setEmail(userRequestDTO.getEmail().toLowerCase());
            logger.debug("Updated email to: {}", userRequestDTO.getEmail());
        }
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            existingUser.setPassword(passwordService.hashPassword(userRequestDTO.getPassword()));
            passwordUpdate = true;
            logger.debug("Updated password.");
        }
        if (userRequestDTO.getPhone() != null && !userRequestDTO.getPhone().isBlank()) {
            existingUser.setPhone(userRequestDTO.getPhone());
            logger.debug("Updated phone to: {}", userRequestDTO.getPhone());
        }
        if (userRequestDTO.getIsActive() != null) {
            existingUser.setActive(userRequestDTO.getIsActive());
            logger.debug("Updated isActive to: {}", userRequestDTO.getIsActive());
        }
        if (userRequestDTO.getStatus() != null && !userRequestDTO.getStatus().isBlank()) {
            existingUser.setStatus(userRequestDTO.getStatus());
            logger.debug("Updated status to: {}", userRequestDTO.getStatus());
        }
        if (userRequestDTO.getTribe() != null && !userRequestDTO.getTribe().isBlank()) {
            existingUser.setTribe(userRequestDTO.getTribe());
            logger.debug("Updated tribe to: {}", userRequestDTO.getTribe());
        }
        if (userRequestDTO.getCurrentCountry() != null && !userRequestDTO.getCurrentCountry().isBlank()) {
            existingUser.setCurrentCountry(userRequestDTO.getCurrentCountry());
            logger.debug("Updated currentCountry to: {}", userRequestDTO.getCurrentCountry());
        }
        if (userRequestDTO.getCurrentCity() != null && !userRequestDTO.getCurrentCity().isBlank()) {
            existingUser.setCurrentCity(userRequestDTO.getCurrentCity());
            logger.debug("Updated currentCity to: {}", userRequestDTO.getCurrentCity());
        }
        if (userRequestDTO.getCurrentVillage() != null && !userRequestDTO.getCurrentVillage().isBlank()) {
            existingUser.setCurrentVillage(userRequestDTO.getCurrentVillage());
            logger.debug("Updated currentVillage to: {}", userRequestDTO.getCurrentVillage());
        }
        if (userRequestDTO.getBirthDate() != null) {
            existingUser.setBirthDate(userRequestDTO.getBirthDate());
            logger.debug("Updated birthDate to: {}", userRequestDTO.getBirthDate());
        }
        if (userRequestDTO.getBirthCountry() != null && !userRequestDTO.getBirthCountry().isBlank()) {
            existingUser.setBirthCountry(userRequestDTO.getBirthCountry());
            logger.debug("Updated birthCountry to: {}", userRequestDTO.getBirthCountry());
        }
        if (userRequestDTO.getBirthCity() != null && !userRequestDTO.getBirthCity().isBlank()) {
            existingUser.setBirthCity(userRequestDTO.getBirthCity());
            logger.debug("Updated birthCity to: {}", userRequestDTO.getBirthCity());
        }
        if (userRequestDTO.getBirthVillage() != null && !userRequestDTO.getBirthVillage().isBlank()) {
            existingUser.setBirthVillage(userRequestDTO.getBirthVillage());
            logger.debug("Updated birthVillage to: {}", userRequestDTO.getBirthVillage());
        }
        if (userRequestDTO.getMaritalStatus() != null && !userRequestDTO.getMaritalStatus().isBlank()) {
            existingUser.setMaritalStatus(userRequestDTO.getMaritalStatus());
            logger.debug("Updated maritalStatus to: {}", userRequestDTO.getMaritalStatus());
        }
        if (userRequestDTO.getNumberOfKids() != null) {
            existingUser.setNumberOfKids(userRequestDTO.getNumberOfKids());
            logger.debug("Updated numberOfKids to: {}", userRequestDTO.getNumberOfKids());
        }
        if (userRequestDTO.getOccupation() != null && !userRequestDTO.getOccupation().isBlank()) {
            existingUser.setOccupation(userRequestDTO.getOccupation());
            logger.debug("Updated occupation to: {}", userRequestDTO.getOccupation());
        }
        if (userRequestDTO.getSex() != null && !userRequestDTO.getSex().isBlank()) {
            existingUser.setSex(userRequestDTO.getSex());
            logger.debug("Updated sex to: {}", userRequestDTO.getSex());
        }
        if (userRequestDTO.getMothersFirstName() != null && !userRequestDTO.getMothersFirstName().isBlank()) {
            existingUser.setMothersFirstName(userRequestDTO.getMothersFirstName());
            logger.debug("Updated mothersFirstName to: {}", userRequestDTO.getMothersFirstName());
        }
        if (userRequestDTO.getMothersLastName() != null && !userRequestDTO.getMothersLastName().isBlank()) {
            existingUser.setMothersLastName(userRequestDTO.getMothersLastName());
            logger.debug("Updated mothersLastName to: {}", userRequestDTO.getMothersLastName());
        }
        if (userRequestDTO.getNationalities() != null && !userRequestDTO.getNationalities().isEmpty()) {
            existingUser.setNationalities(userRequestDTO.getNationalities());
            logger.debug("Updated nationalities to: {}", userRequestDTO.getNationalities());
        }
        if (userRequestDTO.getComments() != null && !userRequestDTO.getComments().isBlank()) {
            existingUser.setComments(userRequestDTO.getComments());
            logger.debug("Updated comments to: {}", userRequestDTO.getComments());
        }
        if (userRequestDTO.getConnectionMethod() != null && !userRequestDTO.getConnectionMethod().isBlank()) {
            existingUser.setCurrentCity(userRequestDTO.getConnectionMethod());
            logger.debug("Updated connectionMethod to: {}", userRequestDTO.getConnectionMethod());
        }
        if(userRequestDTO.getRecommendedBy() != null && !userRequestDTO.getRecommendedBy().isBlank()){
            existingUser.setRecommendedBy(userRequestDTO.getRecommendedBy());
            logger.debug("Updated recommended by  to: {}", userRequestDTO.getRecommendedBy());
        }

        if(userRequestDTO.getApproverComment() != null && !userRequestDTO.getApproverComment().isBlank()){
            existingUser.setApproverComment(userRequestDTO.getApproverComment());
            logger.debug("Updated approver comment to: {}", userRequestDTO.getApproverComment());

        }
        if (userRequestDTO.getRole() != null && !userRequestDTO.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequestDTO.getRole())) {
            logger.debug("Validating role for user with ID: {}", id);
            Optional<Role> optionalRole = roleRepository.findById(userRequestDTO.getRole());
            Role role = optionalRole.orElseThrow(() -> {
                logger.error("Role with ID {} not found for user with ID: {}", userRequestDTO.getRole(), id);
                return new IllegalArgumentException("Role with ID " + userRequestDTO.getRole() + " not found");
            });
            logger.debug("Updating role for user with ID: {}", id);
            existingUser.setRole(role);
        }

        if (userRequestDTO.getIdProofPhotoFront() != null && !userRequestDTO.getIdProofPhotoFront().isBlank()) {
            logger.debug("Updating front ID proof photo for user with ID: {}", id);
            IdProofPhoto idPhotoFront = idPhotoProofRepository.findByUserAndPhotoType(existingUser, "front")
                    .orElse(new IdProofPhoto());
            idPhotoFront.setUser(existingUser);
            idPhotoFront.setPhotoUrl(userRequestDTO.getIdProofPhotoFront());
            idPhotoFront.setPhotoType("front");
            idPhotoFront.setUpdatedAt(LocalDateTime.now());
            idPhotoProofRepository.save(idPhotoFront);
            existingUser.getIdProofPhotos().add(idPhotoFront);
        }

        if (userRequestDTO.getIdProofPhotoBack() != null && !userRequestDTO.getIdProofPhotoBack().isBlank()) {
            logger.debug("Updating back ID proof photo for user with ID: {}", id);
            IdProofPhoto idPhotoBack = idPhotoProofRepository.findByUserAndPhotoType(existingUser, "back")
                    .orElse(new IdProofPhoto());
            idPhotoBack.setUser(existingUser);
            idPhotoBack.setPhotoUrl(userRequestDTO.getIdProofPhotoBack());
            idPhotoBack.setPhotoType("back");
            idPhotoBack.setUpdatedAt(LocalDateTime.now());
            idPhotoProofRepository.save(idPhotoBack);
            existingUser.getIdProofPhotos().add(idPhotoBack);
        }

        if (userRequestDTO.getWaladomCardPhoto() != null && !userRequestDTO.getWaladomCardPhoto().isBlank()) {
            logger.debug("Updating Waladom card photo for user with ID: {}", id);
            WaladomIdPhoto waladomCardPhoto = waladomPhotoRepository.findByUser(existingUser)
                    .orElse(new WaladomIdPhoto());
            waladomCardPhoto.setUser(existingUser);
            waladomCardPhoto.setPhotoUrl(userRequestDTO.getWaladomCardPhoto());
            waladomCardPhoto.setUpdatedAt(LocalDateTime.now());
            waladomPhotoRepository.save(waladomCardPhoto);
            existingUser.setWaladomIdPhoto(waladomCardPhoto);
        }

        logger.info("Saving updated user with ID: {}", id);
        User savedUser = userManagementService.save(existingUser, userRepository);

        if(passwordUpdate){
            emailService.sendPasswordUpdateEmail(existingUser.getEmail().toLowerCase());

        }
        logger.info("User with ID: {} successfully updated.", id);
        return UserResponseDTO.mapToUserResponseDTO(savedUser);
    }

    /**
     * Checks if a UserRequestDTO is empty (all fields are null or blank).
     *
     * @param requestBody The request DTO to check.
     * @return True if the DTO is empty, false otherwise.
     */
    public boolean isEmpty(UserRequestDTO requestBody) {
        logger.info("Entering method isEmpty() for requestBody");

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
        logger.info("Initiating deletion process for user with ID: {}", id);

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            logger.error("User with ID: {} not found. Cannot proceed with deletion.", id);
            throw new RuntimeException("User not found with ID: " + id);
        }

        User user = existingUserOpt.get();
        logger.info("User found. Deleting user record with ID: {}", id);
        userRepository.deleteById(id);

        logger.info("Deleting Waladom ID photo from S3 for user ID: {}", id);
        s3Service.deletePhotoOrFolderFromS3(user.getWaladomIdPhoto().getPhotoUrl());

        logger.info("Deleting first ID proof photo from S3 for user ID: {}", id);
        s3Service.deletePhotoOrFolderFromS3(user.getIdProofPhotos().get(0).getPhotoUrl());

        logger.info("Deleting second ID proof photo from S3 for user ID: {}", id);
        s3Service.deletePhotoOrFolderFromS3(user.getIdProofPhotos().get(1).getPhotoUrl());

        logger.info("User with ID: {} and associated resources successfully deleted.", id);
    }


    /**
     * Validates an email address extracted from the request body.
     *
     * @param email The object containing email details.
     * @return A Map with the validation result.
     */
    public Object validateEmail(String email) {

        logger.info("Starting email validation for email: {}", email);

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            logger.warn("Email validation failed. Email {} is already taken by user ID: {}", email, existingUser.get().getId());
            return Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "User already exists",
                    "message", "The email is already taken",
                    "path", "/api/user/email/validate",
                    "valid", false
            );
        }

        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Performing basic validation for email: {}", email);

            // Perform basic email validation
            if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
                logger.warn("Email validation failed. Invalid format for email: {}", email);
                response.put("valid", false); // Invalid email format
                response.put("message", "Invalid email format");
                return response;
            }

            // Check if the email already exists in the database
            boolean isValid = userRepository.findByEmail(email).isEmpty();
            logger.info("Email validation result for {}: {}", email, isValid ? "Valid" : "Already exists");

            response.put("valid", isValid);
            response.put("message", isValid ? "Email is valid" : "Email already exists");
            return response;
        } catch (Exception e) {
            logger.error("Error occurred during email validation for email {}: {}", email, e.getMessage());
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
        logger.info("Login attempt with identifier: {} and connection method: {}", identifier, connectionMethod);
        Optional<User> userOptional;

        if ("email".equalsIgnoreCase(connectionMethod)) {
            logger.debug("Searching user by email: {}", identifier);
            userOptional = userRepository.findByEmail(identifier.toLowerCase());
        } else if ("phone".equalsIgnoreCase(connectionMethod)) {
            logger.debug("Searching user by phone: {}", identifier);
            userOptional = userRepository.findByPhone(identifier);
        } else {
            logger.warn("Invalid connection method provided: {}", connectionMethod);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid connection method");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, Object> response = new HashMap<>();

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.debug("User found: {}", user.getEmail());

            // Verify the password using PasswordService
            if (!passwordService.verifyPassword(password, user.getPassword())) {
                logger.warn("Password verification failed for user: {}", identifier);
                response.put("valid", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Check if the connection method matches
            if (!connectionMethod.equalsIgnoreCase(user.getConnectionMethod())) {
                logger.warn("Connection method mismatch for user: {}", identifier);
                response.put("valid", false);
                response.put("message", "Invalid connection method for this user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Additional checks for login
            if (!"active".equalsIgnoreCase(user.getStatus())) {
                logger.warn("User account is not active: {}", identifier);
                response.put("valid", false);
                response.put("message", "User account is not active");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (!user.getActive()) {
                logger.warn("User is not active: {}", identifier);
                response.put("valid", false);
                response.put("message", "User is not active");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Generate tokens upon successful login
            String accessToken = jwtTokenUtil.generateAccessToken(user.getEmail(), user.getRole().getName());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getEmail());
            logger.info("Login successful for user: {}", identifier);

            response.put("valid", true);
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("user",UserResponseDTO.mapToUserResponseDTO(userOptional.get()) );

            return ResponseEntity.ok(response);
        } else {
            logger.warn("User not found for identifier: {}", identifier);
            response.put("valid", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



    public Object validatePhone(String phone) {

        logger.info("Starting phone number validation for: {}", phone);

        Optional<User> existingUser = userRepository.findByPhone(phone);
        if (existingUser.isPresent()) {
            logger.warn("Phone validation failed. Phone number {} is already taken by user ID: {}", phone, existingUser.get().getId());
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
            logger.info("Performing basic validation for phone number: {}", phone);

            // Perform basic phone number validation (e.g., E.164 format)
            if (phone == null || !phone.matches("^\\+?[1-9]\\d{1,14}$")) {
                logger.warn("Phone validation failed. Invalid format for phone number: {}", phone);
                response.put("valid", false); // Invalid phone number format
                response.put("message", "Invalid phone number format");
                return response;
            }

            // Check if the phone already exists in the database
            boolean isValid = userRepository.findByPhone(phone).isEmpty();
            logger.info("Phone validation result for {}: {}", phone, isValid ? "Valid" : "Already exists");

            response.put("valid", isValid);
            response.put("message", isValid ? "Phone number is valid" : "Phone number already exists");
            return response;
        } catch (Exception e) {
            logger.error("Error occurred during phone number validation for {}: {}", phone, e.getMessage());
            response.put("valid", false);
            response.put("message", "Error validating phone number: " + e.getMessage());
            return response;
        }
    }

    public List<UserResponseDTO> getUserByFirstNameOrLastNameOrIdOrEmailOrPhone(
            String firstName, String lastName, String id, String email, String phone) {

        // Log input parameters
        logger.info("Searching users by - firstName: {}, lastName: {}, id: {}, email: {}, phone: {}",
                firstName, lastName, id, email, phone);

        List<User> users = userRepository.searchUsers(firstName, lastName, id, email, phone);

        // Log results
        if (users.isEmpty()) {
            logger.warn("No users found for the given search criteria.");
        } else {
            logger.info("Found {} users matching the search criteria.", users.size());
        }

        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    private UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();

        // Basic user details
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
        dto.setRecommendedBy(user.getRecommendedBy());
        dto.setApproverComment(user.getApproverComment());

        // Map WaladomCardPhoto
        if (user.getWaladomIdPhoto() != null) {
            WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
            waladomCardPhotoDTO.setId(user.getWaladomIdPhoto().getId());
            waladomCardPhotoDTO.setPhotoUrl(user.getWaladomIdPhoto().getPhotoUrl());
            waladomCardPhotoDTO.setCreatedAt(user.getWaladomIdPhoto().getCreatedAt());
            waladomCardPhotoDTO.setUpdatedAt(user.getWaladomIdPhoto().getUpdatedAt());
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
        if (user.getRole() != null) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(user.getRole().getId());
            roleDTO.setName(user.getRole().getName());
            roleDTO.setDescription(user.getRole().getDescription());
            roleDTO.setColor(user.getRole().getColor());
            roleDTO.setCreatedAt(user.getRole().getCreatedAt());
            roleDTO.setUpdatedAt(user.getRole().getUpdatedAt());
            dto.setRole(roleDTO);
        }

        return dto;
    }

    public ResponseEntity<?> validatePasswordWithId(Map<String, Object> requestBody){
        Optional<User> existingUserOpt = userRepository.findById(requestBody.get("id").toString());
        if (existingUserOpt.isEmpty()) {
            logger.error("User with ID: {} not found. Cannot proceed with deletion.", requestBody.get("id").toString());
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if( passwordService.verifyPassword(requestBody.get("password").toString(), existingUserOpt.get().getPassword())){
            logger.error("User with ID: {}  found. Valid password.", requestBody.get("id").toString());
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("message", "password valid");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            logger.error("User with ID: {} invalid password.", requestBody.get("id").toString());
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
        logger.info("Starting to save user with email: {}", userRequest.getEmail());

        User newUser = new User();
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPassword(passwordService.hashPassword(userRequest.getPassword())); // Ensure hashing is done here if needed
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

        logger.debug("Populated user object: {}", newUser);

        if (userRequest.getRole() != null && !userRequest.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequest.getRole())) {
            logger.info("Fetching role with ID: {}", userRequest.getRole());
            Optional<Role> optionalRole = roleRepository.findById(userRequest.getRole());

            Role role = optionalRole.orElseThrow(() -> {
                logger.error("Role with ID {} not found", userRequest.getRole());
                return new IllegalArgumentException("Role with ID " + userRequest.getRole() + " not found");
            });

            logger.info("Role with ID {} found: {}", userRequest.getRole(), role);
            newUser.setRole(role);
        }

        logger.info("Saving user to the database");
        return userManagementService.save(newUser, userRepository);
    }

    private void saveWaladomIdPhoto(User user, String photoUrl) {
        logger.info("Saving Waladom ID photo for user ID: {}", user.getId());

        WaladomIdPhoto waladomIdPhoto = new WaladomIdPhoto();
        waladomIdPhoto.setUser(user);
        waladomIdPhoto.setPhotoUrl(photoUrl);
        waladomIdPhoto.setCreatedAt(LocalDateTime.now());
        waladomIdPhoto.setUpdatedAt(LocalDateTime.now());

        waladomPhotoRepository.save(waladomIdPhoto);

        logger.info("Waladom ID photo saved for user ID: {}. Photo URL: {}", user.getId(), photoUrl);
        user.setWaladomIdPhoto(waladomIdPhoto);
    }

    private IdProofPhoto saveIdProofPhoto(User user, String photoUrl, String photoType) {
        logger.info("Saving ID proof photo for user ID: {}. Photo type: {}", user.getId(), photoType);

        IdProofPhoto idProofPhoto = new IdProofPhoto();
        idProofPhoto.setUser(user);
        idProofPhoto.setPhotoUrl(photoUrl);
        idProofPhoto.setPhotoType(photoType);
        idProofPhoto.setCreatedAt(LocalDateTime.now());
        idProofPhoto.setUpdatedAt(LocalDateTime.now());

        idPhotoProofRepository.save(idProofPhoto);

        logger.info("ID proof photo saved for user ID: {}. Photo URL: {}, Photo type: {}", user.getId(), photoUrl, photoType);
        return idProofPhoto;
    }


}