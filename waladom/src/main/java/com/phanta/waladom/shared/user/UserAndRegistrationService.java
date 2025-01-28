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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ReqWaladomPhotoRepository reqWaladomPhotoRepository;
    @Autowired
    private final ReqIdProofRepository reqIdProofRepository;

    @Autowired
    private final PasswordService passwordService;

    @Autowired
    public UserAndRegistrationService(UserManagementService userManagementService, UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository, IdPhotoProofRepository idPhotoProofRepository, WaladomPhotoRepository waladomPhotoRepository, RoleRepository roleRepository, PhotosService photosService, PhotoManagementService photoManagementService, S3Service s3Service, ReqWaladomPhotoRepository reqWaladomPhotoRepository, ReqIdProofRepository reqIdProofRepository, PasswordService passwordService) {
        this.userManagementService = userManagementService;
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.idPhotoProofRepository = idPhotoProofRepository;
        this.waladomPhotoRepository = waladomPhotoRepository;
        this.roleRepository = roleRepository;
        this.photoManagementService = photoManagementService;
        this.s3Service = s3Service;
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
        Optional<RegistrationRequest> existingUserOpt = registrationRequestRepository.findById(requestId);
        userManagementService.delete(requestId, registrationRequestRepository);

        RegistrationRequest registrationRequest = existingUserOpt.get();
        s3Service.deletePhotoOrFolderFromS3(registrationRequest.getReqWaladomPhoto().getPhotoUrl());
        s3Service.deletePhotoOrFolderFromS3(registrationRequest.getReqIdProofPhotos().get(0).getPhotoUrl());
        s3Service.deletePhotoOrFolderFromS3(registrationRequest.getReqIdProofPhotos().get(1).getPhotoUrl());

    }

    public boolean isVaildatedEmpty(UserRequestDTO userRequestDTO  ){
        return userRequestDTO.getValidated() == null || !BOOLEANS.contains( userRequestDTO.getValidated());
    }



    public ResponseEntity<?> updateRegistrationRequest(String id, UserRequestDTO registrationRequestDTO) {
        // Find existing RegistrationRequest by ID
        Optional<RegistrationRequest> existingRequestOpt = registrationRequestRepository.findById(id);

        if (existingRequestOpt.isEmpty()) {
            throw new RuntimeException("Registration request not found with ID: " + id);
        }

        RegistrationRequest existingRequest = existingRequestOpt.get();

        // Track if the validated field was changed
        boolean wasValidatedBefore = existingRequest.getValidated() != null && existingRequest.getValidated();
        boolean isValidatedNow = registrationRequestDTO.getValidated() != null && registrationRequestDTO.getValidated();


        // Update fields based on the values in the DTO if they are not null or blank
        if (registrationRequestDTO.getFirstName() != null && !registrationRequestDTO.getFirstName().isBlank()) {
            existingRequest.setFirstName(registrationRequestDTO.getFirstName());
        }
        if (registrationRequestDTO.getLastName() != null && !registrationRequestDTO.getLastName().isBlank()) {
            existingRequest.setLastName(registrationRequestDTO.getLastName());
        }
        if (registrationRequestDTO.getPassword() != null && !registrationRequestDTO.getPassword().isBlank()) {
            existingRequest.setPassword(registrationRequestDTO.getPassword());
        }
        if (registrationRequestDTO.getEmail() != null && !registrationRequestDTO.getEmail().isBlank()
                && !registrationRequestDTO.getEmail().equals(existingRequest.getEmail())) {
            existingRequest.setEmail(registrationRequestDTO.getEmail());
        }

        if (registrationRequestDTO.getPhone() != null && !registrationRequestDTO.getPhone().isBlank()) {
            existingRequest.setPhone(registrationRequestDTO.getPhone());
        }
        if (registrationRequestDTO.getStatus() != null && !registrationRequestDTO.getStatus().isBlank()) {
            existingRequest.setStatus(registrationRequestDTO.getStatus());
        }
        if (registrationRequestDTO.getCurrentCountry() != null && !registrationRequestDTO.getCurrentCountry().isBlank()) {
            existingRequest.setCurrentCountry(registrationRequestDTO.getCurrentCountry());
        }
        if (registrationRequestDTO.getCurrentCity() != null && !registrationRequestDTO.getCurrentCity().isBlank()) {
            existingRequest.setCurrentCity(registrationRequestDTO.getCurrentCity());
        }
        if (registrationRequestDTO.getCurrentVillage() != null && !registrationRequestDTO.getCurrentVillage().isBlank()) {
            existingRequest.setCurrentVillage(registrationRequestDTO.getCurrentVillage());
        }
        if (registrationRequestDTO.getBirthDate() != null) {
            existingRequest.setBirthDate(registrationRequestDTO.getBirthDate());
        }
        if (registrationRequestDTO.getBirthCountry() != null && !registrationRequestDTO.getBirthCountry().isBlank()) {
            existingRequest.setBirthCountry(registrationRequestDTO.getBirthCountry());
        }
        if (registrationRequestDTO.getBirthCity() != null && !registrationRequestDTO.getBirthCity().isBlank()) {
            existingRequest.setBirthCity(registrationRequestDTO.getBirthCity());
        }
        if (registrationRequestDTO.getBirthVillage() != null && !registrationRequestDTO.getBirthVillage().isBlank()) {
            existingRequest.setBirthVillage(registrationRequestDTO.getBirthVillage());
        }
        if (registrationRequestDTO.getMaritalStatus() != null && !registrationRequestDTO.getMaritalStatus().isBlank()) {
            existingRequest.setMaritalStatus(registrationRequestDTO.getMaritalStatus());
        }
        if (registrationRequestDTO.getNumberOfKids() != null) {
            existingRequest.setNumberOfKids(registrationRequestDTO.getNumberOfKids());
        }
        if (registrationRequestDTO.getOccupation() != null && !registrationRequestDTO.getOccupation().isBlank()) {
            existingRequest.setOccupation(registrationRequestDTO.getOccupation());
        }
        if (registrationRequestDTO.getSex() != null && !registrationRequestDTO.getSex().isBlank()) {
            existingRequest.setSex(registrationRequestDTO.getSex());
        }
        if (registrationRequestDTO.getActive() != null && BOOLEANS.contains(registrationRequestDTO.getActive())) {
            existingRequest.setActive(registrationRequestDTO.getActive());
        }


        if (registrationRequestDTO.getRole() != null && !registrationRequestDTO.getRole().isBlank() && UtilesMethods.isRoleIdValid(registrationRequestDTO.getRole())) {

            // Fetch role by ID
            Optional<Role> optionalRole = roleRepository.findById(registrationRequestDTO.getRole());

            // Handle the Optional
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + registrationRequestDTO.getRole() + " not found"));

            existingRequest.setRole(role);
        }

        // Handle additional field 'isValidated'
        if (registrationRequestDTO.getValidated() != null && BOOLEANS.contains(registrationRequestDTO.getValidated()) ) {
            existingRequest.setValidated(registrationRequestDTO.getValidated());
        }

        // Handle mother's name fields
        if (registrationRequestDTO.getMothersFirstName() != null && !registrationRequestDTO.getMothersFirstName().isBlank()) {
            existingRequest.setMothersFirstName(registrationRequestDTO.getMothersFirstName());
        }
        if (registrationRequestDTO.getMothersLastName() != null && !registrationRequestDTO.getMothersLastName().isBlank()) {
            existingRequest.setMothersLastName(registrationRequestDTO.getMothersLastName());
        }

        // Handle nationalities
        if (registrationRequestDTO.getNationalities() != null && !registrationRequestDTO.getNationalities().isEmpty()) {
            existingRequest.setNationalities(registrationRequestDTO.getNationalities());
        }

        // Handle comments
        if (registrationRequestDTO.getComments() != null && !registrationRequestDTO.getComments().isBlank()) {
            existingRequest.setComments(registrationRequestDTO.getComments());
        }


        // Handle photos

        if (registrationRequestDTO.getIdProofPhotoFront() != null && !registrationRequestDTO.getIdProofPhotoFront().isBlank()) {
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


        // If the registration was not validated before but is now validated, create the user
        if (!wasValidatedBefore && isValidatedNow) {
            User newUser = saveUserFromRegisterationRequest(savedRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User successfully created",
                            "userId", newUser.getId()
                    ));
        }

        return ResponseEntity.ok(UserResponseDTO.mapToRegistrationRequestResponseDTO(savedRequest));


        //return UserResponseDTO.mapToRegistrationRequestResponseDTO(savedRequest);
    }





    @Transactional
    public List<UserResponseDTO> getAllRegistrationRequests() {
        return registrationRequestRepository.findAllWithAssociations()
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
                    dto.setValidated(user.getValidated());
                    dto.setCreatedAt(user.getCreatedAt());
                    dto.setUpdatedAt(user.getUpdatedAt());
                    dto.setConnectionMethod(user.getConnectionMethod());
                    // Map WaladomCardPhoto
                    ReqWaladomIdPhoto waladomCard = user.getReqWaladomPhoto();
                    if (waladomCard != null) {
                        WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
                        waladomCardPhotoDTO.setId(waladomCard.getId());
                        waladomCardPhotoDTO.setPhotoUrl(waladomCard.getPhotoUrl());
                        waladomCardPhotoDTO.setCreatedAt(waladomCard.getCreatedAt());
                        waladomCardPhotoDTO.setUpdatedAt(waladomCard.getUpdatedAt());
                        dto.setWaladomCardPhoto(waladomCardPhotoDTO);
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
    public Optional<UserResponseDTO> getRegistrationById(String id) {
        return registrationRequestRepository.findById(id)
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
                    dto.setValidated(user.getValidated());
                    dto.setCreatedAt(user.getCreatedAt());
                    dto.setUpdatedAt(user.getUpdatedAt());
                    dto.setConnectionMethod(user.getConnectionMethod());

                    // Map WaladomCardPhoto
                    ReqWaladomIdPhoto waladomCard = user.getReqWaladomPhoto();
                    if (waladomCard != null) {
                        WaladomPhotoDTO waladomCardPhotoDTO = new WaladomPhotoDTO();
                        waladomCardPhotoDTO.setId(waladomCard.getId());
                        waladomCardPhotoDTO.setPhotoUrl(waladomCard.getPhotoUrl());
                        waladomCardPhotoDTO.setCreatedAt(waladomCard.getCreatedAt());
                        waladomCardPhotoDTO.setUpdatedAt(waladomCard.getUpdatedAt());
                        dto.setWaladomCardPhoto(waladomCardPhotoDTO);
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




    public Object mapAndSaveUserWithAllDetails(UserRequestDTO userRequest, boolean isUser) {
        if (isUser) {
            // Save User
            User savedUser = saveUser(userRequest);

            // Save Waladom ID Photo
            if (userRequest.getWaladomCardPhoto() != null && !userRequest.getWaladomCardPhoto().isBlank()) {
                saveWaladomIdPhoto(savedUser, userRequest.getWaladomCardPhoto());
            }

            ArrayList<IdProofPhoto> idProofPhotos = new ArrayList<>();

            // Save ID Proof Photos
            if (userRequest.getIdProofPhotoFront() != null && !userRequest.getIdProofPhotoFront().isBlank()) {
                idProofPhotos.add(saveIdProofPhoto(savedUser, userRequest.getIdProofPhotoFront(), "front"));
            }
            if (userRequest.getIdProofPhotoBack() != null && !userRequest.getIdProofPhotoBack().isBlank()) {
                idProofPhotos.add(saveIdProofPhoto(savedUser, userRequest.getIdProofPhotoBack(), "back"));
            }
            savedUser.setIdProofPhotos(idProofPhotos);

            // Return the saved User entity
            return savedUser;
        } else {
            // Save RegistrationRequest
            RegistrationRequest savedRegistrationRequest = saveRegistrationRequest(userRequest);

            // Save Waladom ID Photo for RegistrationRequest
            if (userRequest.getWaladomCardPhoto() != null && !userRequest.getWaladomCardPhoto().isBlank()) {
                saveWaladomIdPhotoForRegistration(savedRegistrationRequest, userRequest.getWaladomCardPhoto());
            }

            ArrayList<ReqIdProof> idProofPhotos = new ArrayList<>();

            // Save ID Proof Photos for RegistrationRequest
            if (userRequest.getIdProofPhotoFront() != null && !userRequest.getIdProofPhotoFront().isBlank()) {
                idProofPhotos.add(saveIdProofPhotoForRegistration(savedRegistrationRequest, userRequest.getIdProofPhotoFront(), "front"));
            }
            if (userRequest.getIdProofPhotoBack() != null && !userRequest.getIdProofPhotoBack().isBlank()) {
                idProofPhotos.add(saveIdProofPhotoForRegistration(savedRegistrationRequest, userRequest.getIdProofPhotoBack(), "back"));
            }
            savedRegistrationRequest.setReqIdProofPhotos(idProofPhotos);

            // Return the saved RegistrationRequest entity
            return savedRegistrationRequest;
        }
    }


    public static String generateUserId(String gender, LocalDate birthDate, String country) {
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

        return userId.toString();
    }



    private User saveUser(UserRequestDTO userRequest) {
        User newUser = new User();
        // Map User fields from DTO
        newUser.setId(generateUserId(userRequest.getSex(), userRequest.getBirthDate(), userRequest.getBirthCountry()));
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
        RegistrationRequest registrationRequest = new RegistrationRequest();
        // Map RegistrationRequest fields from DTO
        registrationRequest.setFirstName(userRequest.getFirstName());
        registrationRequest.setLastName(userRequest.getLastName());
        registrationRequest.setEmail(userRequest.getEmail());
        registrationRequest.setPassword(userRequest.getPassword()); // Ensure hashing is done here if needed
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
        if (userRequest.getRole() != null && !userRequest.getRole().isBlank() && UtilesMethods.isRoleIdValid(userRequest.getRole())) {
            Optional<Role> optionalRole = roleRepository.findById(userRequest.getRole());
            Role role = optionalRole.orElseThrow(() ->
                    new IllegalArgumentException("Role with ID " + userRequest.getRole() + " not found"));
            registrationRequest.setRole(role);
        }
        registrationRequest.setValidated(userRequest.getActive());

        // Save RegistrationRequest via RegistrationRequestService
        return userManagementService.save(registrationRequest, registrationRequestRepository);
    }

    private void saveWaladomIdPhoto(User user, String photoUrl) {
        WaladomIdPhoto waladomIdPhoto = new WaladomIdPhoto();
        waladomIdPhoto.setUser(user);
        waladomIdPhoto.setPhotoUrl(photoUrl);
        waladomIdPhoto.setCreatedAt(LocalDateTime.now());
        waladomIdPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(waladomIdPhoto, waladomPhotoRepository);
        user.setWaladomIdPhoto(waladomIdPhoto);
    }

    private void saveWaladomIdPhotoForRegistration(RegistrationRequest registrationRequest, String photoUrl) {
        ReqWaladomIdPhoto reqWaladomIdPhoto = new ReqWaladomIdPhoto();
        reqWaladomIdPhoto.setRegistrationRequest(registrationRequest);
        reqWaladomIdPhoto.setPhotoUrl(photoUrl);
        reqWaladomIdPhoto.setCreatedAt(LocalDateTime.now());
        reqWaladomIdPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(reqWaladomIdPhoto, reqWaladomPhotoRepository);
        registrationRequest.setReqWaladomPhoto(reqWaladomIdPhoto);
    }

    private IdProofPhoto saveIdProofPhoto(User user, String photoUrl, String photoType) {
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
        ReqIdProof reqIdProofPhoto = new ReqIdProof();
        reqIdProofPhoto.setRegistrationRequest(registrationRequest);
        reqIdProofPhoto.setPhotoUrl(photoUrl);
        reqIdProofPhoto.setPhotoType(photoType);
        reqIdProofPhoto.setCreatedAt(LocalDateTime.now());
        reqIdProofPhoto.setUpdatedAt(LocalDateTime.now());
        photoManagementService.savePhoto(reqIdProofPhoto, reqIdProofRepository);
        return reqIdProofPhoto;
    }



    //@Transactional(rollbackFor = YourCustomException.class)
    @Transactional
    public ResponseEntity<?> createUserOrRegReq(UserRequestDTO userRequest, boolean isUser) {
        try {
            // Check if the email already exists for User creation
            if (isUser) {
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
            }else {
                Optional<RegistrationRequest> existingUser = registrationRequestRepository.findByEmail(userRequest.getEmail());
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
            }

            // Map and save either User or RegistrationRequest based on the isUser flag
            Object savedEntity = mapAndSaveUserWithAllDetails(userRequest, isUser);

            // Return the appropriate response DTO based on whether it's User or RegistrationRequest
            if (isUser) {
                return ResponseEntity.ok(UserResponseDTO.mapToUserResponseDTO((User) savedEntity));
            } else {
                return ResponseEntity.ok(UserResponseDTO.mapToRegistrationRequestResponseDTO((RegistrationRequest) savedEntity));
            }

        } catch (Exception ex) {
            // Return a bad request response with the error message
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
        // Create a new User object
        User user = new User();

        // Set the fields from the RegistrationRequest to the User
        user.setFirstName(registrationRequest.getFirstName());
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
