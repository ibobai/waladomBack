package com.phanta.waladom.shared.user;

import com.phanta.waladom.fileUpload.S3Service;
import com.phanta.waladom.forgotPassword.PasswordService;
import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idPhoto.WaladomPhotoDTO;
import com.phanta.waladom.idPhoto.WaladomPhotoRepository;
import com.phanta.waladom.idProof.IdPhotoProofRepository;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.idProof.IdProofPhotoDTO;
import com.phanta.waladom.registration.RegistrationRequest;
import com.phanta.waladom.registration.RegistrationRequestController;
import com.phanta.waladom.registration.RegistrationRequestRepository;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomIdPhoto;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomPhotoRepository;
import com.phanta.waladom.registration.photos.reqIdProof.ReqIdProof;
import com.phanta.waladom.registration.photos.reqIdProof.ReqIdProofRepository;
import com.phanta.waladom.role.Role;
import com.phanta.waladom.role.RoleDTO;
import com.phanta.waladom.role.RoleRepository;
import com.phanta.waladom.shared.photo.PhotoManagementService;
import com.phanta.waladom.shared.photo.PhotosService;
import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import com.phanta.waladom.user.UserRequestDTO;
import com.phanta.waladom.user.UserResponseDTO;
import com.phanta.waladom.utiles.CountryCodeUtil;
import com.phanta.waladom.utiles.UtilesMethods;
import com.phanta.waladom.verification.email.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.phanta.waladom.utiles.UtilesMethods.BOOLEANS;

@Service
public class UserAndRegistrationService {

    private final UserManagementService userManagementService;
    private final UserRepository userRepository;

    @Autowired
    private final RegistrationRequestRepository registrationRequestRepository;

    @Autowired
    private final IdPhotoProofRepository idPhotoProofRepository;
    @Autowired
    private final WaladomPhotoRepository waladomPhotoRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PhotoManagementService photoManagementService;

    @Autowired
    private final S3Service s3Service;

    @Autowired
    private final EmailService emailService;
    @Autowired
    private final ReqWaladomPhotoRepository reqWaladomPhotoRepository;
    @Autowired
    private final ReqIdProofRepository reqIdProofRepository;

    @Autowired
    private final PasswordService passwordService;

    private static final Logger logger = LogManager.getLogger(RegistrationRequestController.class);

    @Autowired
    public UserAndRegistrationService(UserManagementService userManagementService, UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository, IdPhotoProofRepository idPhotoProofRepository, WaladomPhotoRepository waladomPhotoRepository, RoleRepository roleRepository, PhotosService photosService, PhotoManagementService photoManagementService, S3Service s3Service, EmailService emailService, ReqWaladomPhotoRepository reqWaladomPhotoRepository, ReqIdProofRepository reqIdProofRepository, PasswordService passwordService) {
        this.userManagementService = userManagementService;
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.idPhotoProofRepository = idPhotoProofRepository;
        this.waladomPhotoRepository = waladomPhotoRepository;
        this.roleRepository = roleRepository;
        this.photoManagementService = photoManagementService;
        this.s3Service = s3Service;
        this.emailService = emailService;
        this.reqWaladomPhotoRepository = reqWaladomPhotoRepository;
        this.reqIdProofRepository = reqIdProofRepository;
        this.passwordService = passwordService;
    }

    public User saveUser(User user) {
        return userManagementService.save(user, userRepository);
    }

    public RegistrationRequest saveRegistrationRequest(RegistrationRequest registrationRequest) {
        return userManagementService.save(registrationRequest, registrationRequestRepository);
    }

    public List<User> getAllUsers() {
        return userManagementService.findAll(userRepository);
    }


    public void deleteUser(String userId) {
        userManagementService.delete(userId, userRepository);
    }


    public void deleteRegistrationRequest(String requestId) {
        logger.info("Starting deletion process for registration request with ID: {}", requestId);

        try {
            // Check if the registration request exists
            Optional<RegistrationRequest> existingUserOpt = registrationRequestRepository.findById(requestId);
            if (existingUserOpt.isEmpty()) {
                logger.warn("Registration request with ID: {} does not exist.", requestId);
                return;
            }
            logger.debug("Registration request with ID: {} found.", requestId);

            // Delete the registration request from the repository
            userManagementService.delete(requestId, registrationRequestRepository);
            logger.info("Registration request with ID: {} successfully deleted from the repository.", requestId);

            // Fetch the registration request entity for S3 deletion
            RegistrationRequest registrationRequest = existingUserOpt.get();

            // Delete Waladom card photo from S3
            String waladomPhotoUrl = registrationRequest.getReqWaladomPhoto().getPhotoUrl();
            logger.debug("Deleting Waladom card photo from S3 with URL: {}", waladomPhotoUrl);
            s3Service.deletePhotoOrFolderFromS3(waladomPhotoUrl);
            logger.info("Successfully deleted Waladom card photo from S3 with URL: {}", waladomPhotoUrl);

            // Delete first ID proof photo from S3
            String firstIdPhotoUrl = registrationRequest.getReqIdProofPhotos().get(0).getPhotoUrl();
            logger.debug("Deleting first ID proof photo from S3 with URL: {}", firstIdPhotoUrl);
            s3Service.deletePhotoOrFolderFromS3(firstIdPhotoUrl);
            logger.info("Successfully deleted first ID proof photo from S3 with URL: {}", firstIdPhotoUrl);

            // Delete second ID proof photo from S3
            String secondIdPhotoUrl = registrationRequest.getReqIdProofPhotos().get(1).getPhotoUrl();
            logger.debug("Deleting second ID proof photo from S3 with URL: {}", secondIdPhotoUrl);
            s3Service.deletePhotoOrFolderFromS3(secondIdPhotoUrl);
            logger.info("Successfully deleted second ID proof photo from S3 with URL: {}", secondIdPhotoUrl);

            logger.info("Deletion process completed successfully for registration request with ID: {}", requestId);
            emailService.sendAccountRejectionEmail(registrationRequest.getEmail(), "");
        } catch (Exception ex) {
            logger.error("An error occurred while deleting registration request with ID: {}. Error: {}", requestId, ex.getMessage(), ex);
            throw ex; // Optionally rethrow the exception if necessary
        }
    }

    public boolean isVaildatedEmpty(UserRequestDTO userRequestDTO) {
        logger.info("Checking if 'validated' field in UserRequestDTO is empty or invalid.");

        if (userRequestDTO.getValidated() == null) {
            logger.warn("The 'validated' field is null for UserRequestDTO: {}", userRequestDTO);
            return true;
        }

        boolean isValid = BOOLEANS.contains(userRequestDTO.getValidated());
        if (!isValid) {
            logger.warn("The 'validated' field has an invalid value: {} for UserRequestDTO: {}",
                    userRequestDTO.getValidated(), userRequestDTO);
        } else {
            logger.debug("The 'validated' field has a valid value: {} for UserRequestDTO: {}",
                    userRequestDTO.getValidated(), userRequestDTO);
        }

        logger.info("Validation check completed. Result: {}", !isValid);
        return !isValid;
    }


    public ResponseEntity<?> updateRegistrationRequest(String id, UserRequestDTO registrationRequestDTO) {
        // Find existing RegistrationRequest by ID
        Optional<RegistrationRequest> existingRequestOpt = registrationRequestRepository.findById(id);

        if (existingRequestOpt.isEmpty()) {
            logger.error("Registration request not found with ID: {}", id);
            throw new RuntimeException("Registration request not found with ID: " + id);
        }

        RegistrationRequest existingRequest = existingRequestOpt.get();
        logger.info("Updating RegistrationRequest with ID: {}", id);

        // Track if the validated field was changed
        boolean wasValidatedBefore = existingRequest.getValidated() != null && existingRequest.getValidated();
        boolean isValidatedNow = registrationRequestDTO.getValidated() != null && registrationRequestDTO.getValidated();
        if (wasValidatedBefore != isValidatedNow) {
            logger.info("Validated field changed from {} to {}", wasValidatedBefore, isValidatedNow);
        }
        // Update fields based on the values in the DTO if they are not null or blank
        if (registrationRequestDTO.getFirstName() != null && !registrationRequestDTO.getFirstName().isBlank()) {
            logger.info("Updating first name to: {}", registrationRequestDTO.getFirstName());
            existingRequest.setFirstName(registrationRequestDTO.getFirstName());
        }
        if (registrationRequestDTO.getLastName() != null && !registrationRequestDTO.getLastName().isBlank()) {
            logger.info("Updating last name to: {}", registrationRequestDTO.getLastName());
            existingRequest.setLastName(registrationRequestDTO.getLastName());
        }
        if (registrationRequestDTO.getPassword() != null && !registrationRequestDTO.getPassword().isBlank()) {
            logger.info("Updating password.");
            existingRequest.setPassword(registrationRequestDTO.getPassword());
        }
        if (registrationRequestDTO.getEmail() != null && !registrationRequestDTO.getEmail().isBlank()
                && !registrationRequestDTO.getEmail().equals(existingRequest.getEmail())) {
            logger.info("Updating email: {}", registrationRequestDTO.getEmail());

            existingRequest.setEmail(registrationRequestDTO.getEmail().toLowerCase());
        }

        if (registrationRequestDTO.getPhone() != null && !registrationRequestDTO.getPhone().isBlank()) {
            logger.info("Updating phone: {}", registrationRequestDTO.getPhone());

            existingRequest.setPhone(registrationRequestDTO.getPhone());
        }
        if (registrationRequestDTO.getStatus() != null && !registrationRequestDTO.getStatus().isBlank()) {
            logger.info("Updating status: {}", registrationRequestDTO.getStatus());

            existingRequest.setStatus(registrationRequestDTO.getStatus());
            if(registrationRequestDTO.getStatus().equalsIgnoreCase("rejected") ||
                    registrationRequestDTO.getStatus().equalsIgnoreCase("blocked")
            ){
                emailService.sendAccountRejectionEmail(existingRequest.getEmail(),"");
            }
        }
        if (registrationRequestDTO.getCurrentCountry() != null && !registrationRequestDTO.getCurrentCountry().isBlank()) {
            logger.info("Updating current country: {}", registrationRequestDTO.getCurrentCountry());

            existingRequest.setCurrentCountry(registrationRequestDTO.getCurrentCountry());
        }
        if (registrationRequestDTO.getCurrentCity() != null && !registrationRequestDTO.getCurrentCity().isBlank()) {
            logger.info("Updating current city : {}", registrationRequestDTO.getCurrentCity());

            existingRequest.setCurrentCity(registrationRequestDTO.getCurrentCity());
        }
        if (registrationRequestDTO.getCurrentVillage() != null && !registrationRequestDTO.getCurrentVillage().isBlank()) {
            logger.info("Updating current village: {}", registrationRequestDTO.getCurrentVillage());

            existingRequest.setCurrentVillage(registrationRequestDTO.getCurrentVillage());
        }
        if (registrationRequestDTO.getBirthDate() != null) {
            logger.info("Updating birth date to: {}", registrationRequestDTO.getBirthDate());
            existingRequest.setBirthDate(registrationRequestDTO.getBirthDate());
        }
        if (registrationRequestDTO.getBirthCountry() != null && !registrationRequestDTO.getBirthCountry().isBlank()) {
            logger.info("Updating birth country to: {}", registrationRequestDTO.getBirthCountry());
            existingRequest.setBirthCountry(registrationRequestDTO.getBirthCountry());
        }
        if (registrationRequestDTO.getBirthCity() != null && !registrationRequestDTO.getBirthCity().isBlank()) {
            logger.info("Updating birth city to: {}", registrationRequestDTO.getBirthCity());
            existingRequest.setBirthCity(registrationRequestDTO.getBirthCity());
        }
        if (registrationRequestDTO.getBirthVillage() != null && !registrationRequestDTO.getBirthVillage().isBlank()) {
            logger.info("Updating birth village to: {}", registrationRequestDTO.getBirthVillage());
            existingRequest.setBirthVillage(registrationRequestDTO.getBirthVillage());
        }
        if (registrationRequestDTO.getMaritalStatus() != null && !registrationRequestDTO.getMaritalStatus().isBlank()) {
            logger.info("Updating marital status to: {}", registrationRequestDTO.getMaritalStatus());
            existingRequest.setMaritalStatus(registrationRequestDTO.getMaritalStatus());
        }
        if (registrationRequestDTO.getNumberOfKids() != null) {
            logger.info("Updating number of kids to: {}", registrationRequestDTO.getNumberOfKids());
            existingRequest.setNumberOfKids(registrationRequestDTO.getNumberOfKids());
        }
        if (registrationRequestDTO.getOccupation() != null && !registrationRequestDTO.getOccupation().isBlank()) {
            logger.info("Updating occupation to: {}", registrationRequestDTO.getOccupation());
            existingRequest.setOccupation(registrationRequestDTO.getOccupation());
        }
        if (registrationRequestDTO.getSex() != null && !registrationRequestDTO.getSex().isBlank()) {
            logger.info("Updating sex to: {}", registrationRequestDTO.getSex());
            existingRequest.setSex(registrationRequestDTO.getSex());
        }
        if (registrationRequestDTO.getActive() != null && BOOLEANS.contains(registrationRequestDTO.getActive())) {
            logger.info("Updating active status to: {}", registrationRequestDTO.getActive());
            existingRequest.setActive(registrationRequestDTO.getActive());
        }

        if (registrationRequestDTO.getRole() != null && !registrationRequestDTO.getRole().isBlank() && UtilesMethods.isRoleIdValid(registrationRequestDTO.getRole())) {
            // Fetch role by ID
            Optional<Role> optionalRole = roleRepository.findById(registrationRequestDTO.getRole());

            // Handle the Optional
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + registrationRequestDTO.getRole() + " not found"));

            logger.info("Assigning role: {}", role.getName());
            existingRequest.setRole(role);
        }

// Handle additional field 'isValidated'
        if (registrationRequestDTO.getValidated() != null && BOOLEANS.contains(registrationRequestDTO.getValidated())) {
            logger.info("Updating validated status to: {}", registrationRequestDTO.getValidated());
            existingRequest.setValidated(registrationRequestDTO.getValidated());
        }

// Handle mother's name fields
        if (registrationRequestDTO.getMothersFirstName() != null && !registrationRequestDTO.getMothersFirstName().isBlank()) {
            logger.info("Updating mother's first name to: {}", registrationRequestDTO.getMothersFirstName());
            existingRequest.setMothersFirstName(registrationRequestDTO.getMothersFirstName());
        }
        if (registrationRequestDTO.getMothersLastName() != null && !registrationRequestDTO.getMothersLastName().isBlank()) {
            logger.info("Updating mother's last name to: {}", registrationRequestDTO.getMothersLastName());
            existingRequest.setMothersLastName(registrationRequestDTO.getMothersLastName());
        }

        // Handle nationalities
        if (registrationRequestDTO.getNationalities() != null && !registrationRequestDTO.getNationalities().isEmpty()) {
            logger.info("Updating nationalities to: {}", registrationRequestDTO.getNationalities());
            existingRequest.setNationalities(registrationRequestDTO.getNationalities());
        }

        // Handle comments
        if (registrationRequestDTO.getComments() != null && !registrationRequestDTO.getComments().isBlank()) {
            logger.info("Updating comments to: {}", registrationRequestDTO.getComments());
            existingRequest.setComments(registrationRequestDTO.getComments());
        }

        if(registrationRequestDTO.getApproverComment() != null && !registrationRequestDTO.getApproverComment().isBlank()){
            logger.info("Updating approver to: {}", registrationRequestDTO.getApproverComment());
            existingRequest.setApproverComment(registrationRequestDTO.getApproverComment());
        }

        if(registrationRequestDTO.getRecommendedBy() != null && !registrationRequestDTO.getRecommendedBy().isBlank()){
            logger.info("Updating recommended by to: {}", registrationRequestDTO.getRecommendedBy());
            existingRequest.setRecommendedBy(registrationRequestDTO.getRecommendedBy());
        }


        // Handle photos with appropriate logging
        if (registrationRequestDTO.getIdProofPhotoFront() != null && !registrationRequestDTO.getIdProofPhotoFront().isBlank()) {
            logger.debug("Updating front ID proof photo");
            ReqIdProof idPhotoFront = reqIdProofRepository.findByRegistrationRequestAndPhotoType(existingRequest, "front")
                    .orElse(new ReqIdProof());
            idPhotoFront.setRegistrationRequest(existingRequest);
            idPhotoFront.setPhotoUrl(registrationRequestDTO.getIdProofPhotoFront());
            idPhotoFront.setPhotoType("front");
            idPhotoFront.setUpdatedAt(LocalDateTime.now());
            reqIdProofRepository.save(idPhotoFront);
            existingRequest.getReqIdProofPhotos().add(idPhotoFront); // Add directly to the existing collection
        }
        if (registrationRequestDTO.getIdProofPhotoBack() != null && !registrationRequestDTO.getIdProofPhotoBack().isBlank()) {
            logger.debug("Updating back ID proof photo");
            ReqIdProof idPhotoBack = reqIdProofRepository.findByRegistrationRequestAndPhotoType(existingRequest, "back")
                    .orElse(new ReqIdProof());
            idPhotoBack.setRegistrationRequest(existingRequest);
            idPhotoBack.setPhotoUrl(registrationRequestDTO.getIdProofPhotoBack());
            idPhotoBack.setPhotoType("back");
            idPhotoBack.setUpdatedAt(LocalDateTime.now());
            reqIdProofRepository.save(idPhotoBack);
            existingRequest.getReqIdProofPhotos().add(idPhotoBack); // Add directly to the existing collection
        }

        if (registrationRequestDTO.getWaladomCardPhoto() != null && !registrationRequestDTO.getWaladomCardPhoto().isBlank()) {
            logger.debug("Updating Waladom card photo");
            ReqWaladomIdPhoto waladomCardPhoto = reqWaladomPhotoRepository.findByRegistrationRequest(existingRequest)
                    .orElse(new ReqWaladomIdPhoto());
            waladomCardPhoto.setRegistrationRequest(existingRequest);
            waladomCardPhoto.setPhotoUrl(registrationRequestDTO.getWaladomCardPhoto());
            waladomCardPhoto.setUpdatedAt(LocalDateTime.now());
            reqWaladomPhotoRepository.save(waladomCardPhoto);
            existingRequest.setReqWaladomPhoto(waladomCardPhoto);
        }

        // Save and return the updated registration request
        RegistrationRequest savedRequest = userManagementService.save(existingRequest, registrationRequestRepository);
        logger.info("Registration request updated successfully for ID: {}", id);

        // If the registration was not validated before but is now validated, create the user
        if (!wasValidatedBefore && isValidatedNow) {
            logger.info("Creating user as the registration request has been validated");
            User newUser = saveUserFromRegisterationRequest(savedRequest);

            //informing user that their account has been validated and now they can login !
            emailService.sendAccountValidationEmail(newUser.getEmail());

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "User successfully created",
                            "userId", newUser.getId()
                    ));
        }

        logger.info("Returning updated registration request for ID: {}", id);
        return ResponseEntity.ok(UserResponseDTO.mapToRegistrationRequestResponseDTO(savedRequest));
    }


    @Transactional
    public List<UserResponseDTO> getAllRegistrationRequests() {
        Logger logger = LogManager.getLogger(RegistrationRequestController.class);

        logger.info("Starting getAllRegistrationRequests method.");

        try {
            logger.debug("Fetching all registration requests with associations from the repository.");
            return registrationRequestRepository.findAllWithAssociations()
                    .stream()
                    .map(user -> {
                        logger.debug("Mapping user with ID: {} and email: {} to UserResponseDTO.", user.getId(), user.getEmail());

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
                        dto.setValidated(user.getValidated());
                        dto.setCreatedAt(user.getCreatedAt());
                        dto.setUpdatedAt(user.getUpdatedAt());
                        dto.setConnectionMethod(user.getConnectionMethod());

                        dto.setApproverComment(user.getApproverComment());
                        dto.setRecommendedBy(user.getRecommendedBy());

                        // Map WaladomCardPhoto
                        ReqWaladomIdPhoto waladomCard = user.getReqWaladomPhoto();
                        if (waladomCard != null) {
                            logger.debug("Mapping WaladomCardPhoto for user ID: {}.", user.getId());
                            WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
                            waladomCardPhotoDTO.setId(waladomCard.getId());
                            waladomCardPhotoDTO.setPhotoUrl(waladomCard.getPhotoUrl());
                            waladomCardPhotoDTO.setCreatedAt(waladomCard.getCreatedAt());
                            waladomCardPhotoDTO.setUpdatedAt(waladomCard.getUpdatedAt());
                            dto.setWaladomCardPhoto(waladomCardPhotoDTO);
                        }

                        // Map IdProofPhotos
                        logger.debug("Mapping ID proof photos for user ID: {}.", user.getId());
                        List<IdProofPhotoDTO> idProofPhotoDTOs = user.getReqIdProofPhotos().stream()
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
                            logger.debug("Mapping role for user ID: {}.", user.getId());
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
        } catch (Exception ex) {
            logger.error("Error occurred while fetching or mapping registration requests: {}", ex.getMessage(), ex);
            throw ex;
        } finally {
            logger.info("Completed getAllRegistrationRequests method.");
        }
    }


    @Transactional
    public Optional<UserResponseDTO> getRegistrationById(String id) {
        logger.info("Fetching registration details for ID: {}", id);

        try {
            return registrationRequestRepository.findById(id)
                    .map(user -> {
                        logger.debug("Registration found for ID: {}", id);
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
                        dto.setValidated(user.getValidated());
                        dto.setCreatedAt(user.getCreatedAt());
                        dto.setUpdatedAt(user.getUpdatedAt());
                        dto.setConnectionMethod(user.getConnectionMethod());

                        dto.setApproverComment(user.getApproverComment());
                        dto.setRecommendedBy(user.getRecommendedBy());

                        logger.debug("Basic user details mapped for ID: {}", id);

                        // Map WaladomCardPhoto
                        ReqWaladomIdPhoto waladomCard = user.getReqWaladomPhoto();
                        if (waladomCard != null) {
                            WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
                            waladomCardPhotoDTO.setId(waladomCard.getId());
                            waladomCardPhotoDTO.setPhotoUrl(waladomCard.getPhotoUrl());
                            waladomCardPhotoDTO.setCreatedAt(waladomCard.getCreatedAt());
                            waladomCardPhotoDTO.setUpdatedAt(waladomCard.getUpdatedAt());
                            dto.setWaladomCardPhoto(waladomCardPhotoDTO);
                            logger.debug("Waladom card photo mapped for ID: {}", id);
                        }

                        // Map IdProofPhotos
                        List<IdProofPhotoDTO> idProofPhotoDTOs = user.getReqIdProofPhotos().stream()
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
                        logger.debug("ID proof photos mapped for ID: {}", id);

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
                            logger.debug("Role details mapped for ID: {}", id);
                        }

                        logger.info("Successfully mapped all registration details for ID: {}", id);
                        return dto;
                    });
        } catch (Exception ex) {
            logger.error("An error occurred while fetching registration details for ID: {}. Error: {}", id, ex.getMessage(), ex);
            throw ex; // Re-throw the exception if necessary
        }
    }



    public Object mapAndSaveUserWithAllDetails(UserRequestDTO userRequest, boolean isUser) {
        logger.info("Mapping and saving user details. isUser: {}", isUser);

        if (isUser) {
            logger.info("Saving User...");

            // Save User
            User savedUser = saveUser(userRequest);
            logger.info("User saved with ID: {}", savedUser.getId());

            // Save Waladom ID Photo
            if (userRequest.getWaladomCardPhoto() != null && !userRequest.getWaladomCardPhoto().isBlank()) {
                logger.info("Saving Waladom ID Photo for User...");
                saveWaladomIdPhoto(savedUser, userRequest.getWaladomCardPhoto());
                logger.info("Waladom ID Photo saved for User ID: {}", savedUser.getId());
            }

            ArrayList<IdProofPhoto> idProofPhotos = new ArrayList<>();

            // Save ID Proof Photos
            if (userRequest.getIdProofPhotoFront() != null && !userRequest.getIdProofPhotoFront().isBlank()) {
                logger.info("Saving front ID Proof Photo for User...");
                idProofPhotos.add(saveIdProofPhoto(savedUser, userRequest.getIdProofPhotoFront(), "front"));
                logger.info("Front ID Proof Photo saved for User ID: {}", savedUser.getId());
            }
            if (userRequest.getIdProofPhotoBack() != null && !userRequest.getIdProofPhotoBack().isBlank()) {
                logger.info("Saving back ID Proof Photo for User...");
                idProofPhotos.add(saveIdProofPhoto(savedUser, userRequest.getIdProofPhotoBack(), "back"));
                logger.info("Back ID Proof Photo saved for User ID: {}", savedUser.getId());
            }
            savedUser.setIdProofPhotos(idProofPhotos);

            // Return the saved User entity
            logger.info("Returning saved User entity with ID: {}", savedUser.getId());
            return savedUser;
        } else {
            logger.info("Saving RegistrationRequest...");

            // Save RegistrationRequest
            RegistrationRequest savedRegistrationRequest = saveRegistrationRequest(userRequest);
            logger.info("RegistrationRequest saved with ID: {}", savedRegistrationRequest.getId());

            // Save Waladom ID Photo for RegistrationRequest
            if (userRequest.getWaladomCardPhoto() != null && !userRequest.getWaladomCardPhoto().isBlank()) {
                logger.info("Saving Waladom ID Photo for RegistrationRequest...");
                saveWaladomIdPhotoForRegistration(savedRegistrationRequest, userRequest.getWaladomCardPhoto());
                logger.info("Waladom ID Photo saved for RegistrationRequest ID: {}", savedRegistrationRequest.getId());
            }

            ArrayList<ReqIdProof> idProofPhotos = new ArrayList<>();

            // Save ID Proof Photos for RegistrationRequest
            if (userRequest.getIdProofPhotoFront() != null && !userRequest.getIdProofPhotoFront().isBlank()) {
                logger.info("Saving front ID Proof Photo for RegistrationRequest...");
                idProofPhotos.add(saveIdProofPhotoForRegistration(savedRegistrationRequest, userRequest.getIdProofPhotoFront(), "front"));
                logger.info("Front ID Proof Photo saved for RegistrationRequest ID: {}", savedRegistrationRequest.getId());
            }
            if (userRequest.getIdProofPhotoBack() != null && !userRequest.getIdProofPhotoBack().isBlank()) {
                logger.info("Saving back ID Proof Photo for RegistrationRequest...");
                idProofPhotos.add(saveIdProofPhotoForRegistration(savedRegistrationRequest, userRequest.getIdProofPhotoBack(), "back"));
                logger.info("Back ID Proof Photo saved for RegistrationRequest ID: {}", savedRegistrationRequest.getId());
            }
            savedRegistrationRequest.setReqIdProofPhotos(idProofPhotos);

            // Return the saved RegistrationRequest entity
            logger.info("Returning saved RegistrationRequest entity with ID: {}", savedRegistrationRequest.getId());
            return savedRegistrationRequest;
        }
    }

    public static String generateUserId(String gender, LocalDate birthDate, String country) {
        logger.info("Entering the method generateUserId()");

        StringBuilder userId = new StringBuilder("WLD_");

        // Gender: M -> 1, F -> 2, O -> 3
        switch (gender.toUpperCase()) {
            case "M" -> userId.append("1");
            case "F" -> userId.append("2");
            default -> userId.append("3");
        }

        // Extract birth month and year from birthDate
        int birthMonth = birthDate.getMonthValue();  // Get birth month (1-12)
        int birthYear = birthDate.getYear();        // Get birth year (e.g., 1996)

        // Add birth month (two digits)
        userId.append(String.format("%02d", birthMonth));

        // Add last two digits of birth year
        userId.append(String.format("%02d", birthYear % 100));

        // Add country code
        String countryCode = CountryCodeUtil.getCountryCode(country); // Assume this method returns a valid country code
        userId.append(countryCode);

        // Add last two digits of the current year (join year)
        int currentYear = LocalDate.now().getYear(); // Get the current year
        userId.append(String.format("%02d", currentYear % 100));

        // Add two random digits
        Random random = new Random();
        userId.append(random.nextInt(10)).append(random.nextInt(10));
        logger.info("User id generated : {}", userId);

        return userId.toString();
    }



    private User saveUser(UserRequestDTO userRequest) {
        logger.info("Saving user in method saveUser()");

        User newUser = new User();
        // Map User fields from DTO
        newUser.setId(generateUserId(userRequest.getSex(), userRequest.getBirthDate(), userRequest.getBirthCountry()));
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setEmail(userRequest.getEmail().toLowerCase());
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

        newUser.setApproverComment(userRequest.getApproverComment());
        newUser.setRecommendedBy(userRequest.getRecommendedBy());

        if (userRequest.getRole() != null && !userRequest.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequest.getRole())) {
            Optional<Role> optionalRole = roleRepository.findById(userRequest.getRole());
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + userRequest.getRole() + " not found"));
            newUser.setRole(role);
        }

        // Save User via UserManagementService
        return userManagementService.save(newUser, userRepository);
    }

    private RegistrationRequest saveRegistrationRequest(UserRequestDTO userRequest) {
        logger.info("Saving registration request in method saveRegistrationRequest()");

        RegistrationRequest registrationRequest = new RegistrationRequest();
        // Map RegistrationRequest fields from DTO
        registrationRequest.setFirstName(userRequest.getFirstName());
        registrationRequest.setLastName(userRequest.getLastName());
        registrationRequest.setEmail(userRequest.getEmail());
        registrationRequest.setPassword(passwordService.hashPassword(userRequest.getPassword())); // Ensure hashing is done here if needed
        registrationRequest.setPhone(userRequest.getPhone());
        registrationRequest.setStatus(userRequest.getStatus());
        registrationRequest.setActive(userRequest.getIsActive());
        registrationRequest.setTribe(userRequest.getTribe());
        registrationRequest.setBirthDate(userRequest.getBirthDate());
        registrationRequest.setCurrentCountry(userRequest.getCurrentCountry());
        registrationRequest.setCurrentCity(userRequest.getCurrentCity());
        registrationRequest.setCurrentVillage(userRequest.getCurrentVillage());
        registrationRequest.setBirthCountry(userRequest.getBirthCountry());
        registrationRequest.setBirthCity(userRequest.getBirthCity());
        registrationRequest.setBirthVillage(userRequest.getBirthVillage());
        registrationRequest.setMaritalStatus(userRequest.getMaritalStatus());
        registrationRequest.setNumberOfKids(userRequest.getNumberOfKids());
        registrationRequest.setOccupation(userRequest.getOccupation());
        registrationRequest.setSex(userRequest.getSex());
        registrationRequest.setMothersFirstName(userRequest.getMothersFirstName());
        registrationRequest.setMothersLastName(userRequest.getMothersLastName());
        registrationRequest.setNationalities(userRequest.getNationalities());
        registrationRequest.setConnectionMethod(userRequest.getConnectionMethod());
        registrationRequest.setComments(userRequest.getComments());

        registrationRequest.setRecommendedBy(userRequest.getRecommendedBy());
        registrationRequest.setApproverComment(userRequest.getApproverComment());

        if (userRequest.getRole() != null && !userRequest.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequest.getRole())) {
            Optional<Role> optionalRole = roleRepository.findById(userRequest.getRole());
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + userRequest.getRole() + " not found"));
            registrationRequest.setRole(role);
        }
        registrationRequest.setValidated(false);

        // Save RegistrationRequest via RegistrationRequestService
        return userManagementService.save(registrationRequest, registrationRequestRepository);
    }

    private void saveWaladomIdPhoto(User user, String photoUrl) {
        logger.info("Saving saveWaladomIdPhoto, method saveWaladomIdPhoto()");

        WaladomIdPhoto waladomIdPhoto = new WaladomIdPhoto();
        waladomIdPhoto.setUser(user);
        waladomIdPhoto.setPhotoUrl(photoUrl);
        waladomIdPhoto.setCreatedAt(LocalDateTime.now());
        waladomIdPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(waladomIdPhoto, waladomPhotoRepository);
        user.setWaladomIdPhoto(waladomIdPhoto);
    }

    private void saveWaladomIdPhotoForRegistration(RegistrationRequest registrationRequest, String photoUrl) {
        logger.info("Saving saveWaladomIdPhotoForRegistration, method saveWaladomIdPhotoForRegistration()");
        ReqWaladomIdPhoto reqWaladomIdPhoto = new ReqWaladomIdPhoto();
        reqWaladomIdPhoto.setRegistrationRequest(registrationRequest);
        reqWaladomIdPhoto.setPhotoUrl(photoUrl);
        reqWaladomIdPhoto.setCreatedAt(LocalDateTime.now());
        reqWaladomIdPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(reqWaladomIdPhoto, reqWaladomPhotoRepository);
        registrationRequest.setReqWaladomPhoto(reqWaladomIdPhoto);
    }

    private IdProofPhoto saveIdProofPhoto(User user, String photoUrl, String photoType) {
        logger.info("Saving saveIdProofPhoto, method saveIdProofPhoto()");

        IdProofPhoto idProofPhoto = new IdProofPhoto();
        idProofPhoto.setUser(user);
        idProofPhoto.setPhotoUrl(photoUrl);
        idProofPhoto.setPhotoType(photoType);
        idProofPhoto.setCreatedAt(LocalDateTime.now());
        idProofPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(idProofPhoto, idPhotoProofRepository);
        return idProofPhoto;
    }

    private ReqIdProof saveIdProofPhotoForRegistration(RegistrationRequest registrationRequest, String photoUrl, String photoType) {
        logger.info("Saving saveIdProofPhotoForRegistration, method saveIdProofPhotoForRegistration()");

        ReqIdProof reqIdProofPhoto = new ReqIdProof();
        reqIdProofPhoto.setRegistrationRequest(registrationRequest);
        reqIdProofPhoto.setPhotoUrl(photoUrl);
        reqIdProofPhoto.setPhotoType(photoType);
        reqIdProofPhoto.setCreatedAt(LocalDateTime.now());
        reqIdProofPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(reqIdProofPhoto, reqIdProofRepository);
        return reqIdProofPhoto;
    }



    @Transactional
    public ResponseEntity<?> createUserOrRegReq(UserRequestDTO userRequest, boolean isUser) {
        Logger logger = LogManager.getLogger(RegistrationRequestController.class);

        logger.info("Starting the createUserOrRegReq method for {}. isUser: {}", userRequest.getEmail(), isUser);

        try {
            if (isUser) {
                logger.debug("Checking if the email {} already exists in the User repository", userRequest.getEmail());
                Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
                if (existingUser.isPresent()) {
                    logger.warn("User already exists with email: {} and ID: {}", userRequest.getEmail(), existingUser.get().getId());
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
            } else {
                logger.debug("Checking if the email {} already exists in the RegistrationRequest repository", userRequest.getEmail());
                Optional<RegistrationRequest> existingUser = registrationRequestRepository.findByEmail(userRequest.getEmail());
                if (existingUser.isPresent()) {
                    logger.warn("Registration request already exists with email: {} and ID: {}", userRequest.getEmail(), existingUser.get().getId());
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
            }

            logger.debug("Mapping and saving entity for email: {}", userRequest.getEmail());
            Object savedEntity = mapAndSaveUserWithAllDetails(userRequest, isUser);

            if (isUser) {
                logger.info("User created successfully for email: {}. Returning UserResponseDTO.", userRequest.getEmail());
                return ResponseEntity.ok(UserResponseDTO.mapToUserResponseDTO((User) savedEntity));
            } else {
                RegistrationRequest newUser = (RegistrationRequest) savedEntity;
                emailService.sendRegistrationConfirmationEmail(newUser.getEmail(), newUser.getId() );
                logger.info("Registration request created successfully for email: {}. Returning RegistrationRequestResponseDTO.", userRequest.getEmail());
                return ResponseEntity.ok(UserResponseDTO.mapToRegistrationRequestResponseDTO(newUser));
            }
        } catch (Exception ex) {
            logger.error("Error occurred while creating user or registration request for email: {}. Error: {}", userRequest.getEmail(), ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Unable to create user or registration request",
                            "message", ex.getMessage(),
                            "path", "/api/user/createDTO"
                    )
            );
        }
    }

    public User saveUserFromRegisterationRequest(RegistrationRequest registrationRequest) {
        logger.info("Mapping user from registrationReqeust, method saveUserFromRegisterationRequest()");

        // Create a new User object
        User user = new User();

        // Set the fields from the RegistrationRequest to the User
        user.setFirstName(registrationRequest.getFirstName());
        user.setId(generateUserId(registrationRequest.getSex(),registrationRequest.getBirthDate(),registrationRequest.getBirthCountry()));
        user.setLastName(registrationRequest.getLastName());
        user.setEmail(registrationRequest.getEmail());
        user.setPhone(registrationRequest.getPhone());
        user.setActive(registrationRequest.getActive());
        user.setStatus(registrationRequest.getStatus());
        user.setTribe(registrationRequest.getTribe());
        user.setCurrentCountry(registrationRequest.getCurrentCountry());
        user.setCurrentCity(registrationRequest.getCurrentCity());
        user.setCurrentVillage(registrationRequest.getCurrentVillage());
        user.setBirthDate(registrationRequest.getBirthDate());
        user.setBirthCountry(registrationRequest.getBirthCountry());
        user.setBirthCity(registrationRequest.getBirthCity());
        user.setBirthVillage(registrationRequest.getBirthVillage());
        user.setPassword(registrationRequest.getPassword());
        user.setMaritalStatus(registrationRequest.getMaritalStatus());
        user.setNumberOfKids(registrationRequest.getNumberOfKids());
        user.setOccupation(registrationRequest.getOccupation());
        user.setSex(registrationRequest.getSex());
        user.setMothersFirstName(registrationRequest.getMothersFirstName());
        user.setMothersLastName(registrationRequest.getMothersLastName());
        user.setNationalities(registrationRequest.getNationalities());
        user.setComments(registrationRequest.getComments());
        user.setConnectionMethod(registrationRequest.getConnectionMethod());

        user.setApproverComment(registrationRequest.getApproverComment());
        user.setRecommendedBy(registrationRequest.getRecommendedBy());

        if (registrationRequest.getRole() != null && !registrationRequest.getRole().getId().isBlank() && UtilesMethods.isRoleIdValid(registrationRequest.getRole().getId())) {

            // Fetch role by ID
            Optional<Role> optionalRole = roleRepository.findById(registrationRequest.getRole().getId());

            // Handle the Optional
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + registrationRequest.getRole().getId() + " not found"));

            user.setRole(role);
        }
        userManagementService.save(user, userRepository);

        if (registrationRequest.getReqWaladomPhoto() != null) {

            WaladomIdPhoto waladomIdPhoto = new WaladomIdPhoto();
            waladomIdPhoto.setUser(user);
            waladomIdPhoto.setPhotoUrl(registrationRequest.getReqWaladomPhoto().getPhotoUrl());
            waladomIdPhoto.setCreatedAt(LocalDateTime.now());
            waladomIdPhoto.setUpdatedAt(LocalDateTime.now());
            waladomPhotoRepository.save(waladomIdPhoto);
            user.setWaladomIdPhoto(waladomIdPhoto);
        }

        if (registrationRequest.getReqIdProofPhotos() != null) {

            ArrayList<IdProofPhoto> idProofPhotos = new ArrayList<>();

            for (ReqIdProof photo : registrationRequest.getReqIdProofPhotos()) {

                if ("front".equals(photo.getPhotoType())) {

                    IdProofPhoto idProofPhoto = new IdProofPhoto();
                    idProofPhoto.setUser(user);
                    idProofPhoto.setPhotoUrl(photo.getPhotoUrl());
                    idProofPhoto.setPhotoType(photo.getPhotoType());
                    idProofPhoto.setCreatedAt(LocalDateTime.now());
                    idProofPhoto.setUpdatedAt(LocalDateTime.now());
                    idPhotoProofRepository.save(idProofPhoto);
                    idProofPhotos.add(idProofPhoto);

                } else if ("back".equals(photo.getPhotoType())) {
                    IdProofPhoto idProofPhoto = new IdProofPhoto();
                    idProofPhoto.setUser(user);
                    idProofPhoto.setPhotoUrl(photo.getPhotoUrl());
                    idProofPhoto.setPhotoType(photo.getPhotoType());
                    idProofPhoto.setCreatedAt(LocalDateTime.now());
                    idProofPhoto.setUpdatedAt(LocalDateTime.now());
                    idPhotoProofRepository.save(idProofPhoto);

                    idProofPhotos.add(idProofPhoto);
                }

            }
            user.setIdProofPhotos(idProofPhotos);

        }

        return user;

    }



}
