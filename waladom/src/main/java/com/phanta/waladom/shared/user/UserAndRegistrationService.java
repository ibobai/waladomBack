package com.phanta.waladom.shared.user;

import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idPhoto.WaladomPhotoDTO;
import com.phanta.waladom.idPhoto.WaladomPhotoRepository;
import com.phanta.waladom.idProof.IdPhotoProofRepository;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.idProof.IdProofPhotoDTO;
import com.phanta.waladom.registration.RegistrationRequest;
import com.phanta.waladom.registration.RegistrationRequestRepository;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomPhoto;
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
import com.phanta.waladom.utiles.UtilesMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ReqWaladomPhotoRepository reqWaladomPhotoRepository;
    @Autowired
    private final ReqIdProofRepository reqIdProofRepository;

    @Autowired
    public UserAndRegistrationService(UserManagementService userManagementService, UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository, IdPhotoProofRepository idPhotoProofRepository, WaladomPhotoRepository waladomPhotoRepository, RoleRepository roleRepository, PhotosService photosService, PhotoManagementService photoManagementService, ReqWaladomPhotoRepository reqWaladomPhotoRepository, ReqIdProofRepository reqIdProofRepository) {
        this.userManagementService = userManagementService;
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.idPhotoProofRepository = idPhotoProofRepository;
        this.waladomPhotoRepository = waladomPhotoRepository;
        this.roleRepository = roleRepository;
        this.photoManagementService = photoManagementService;
        this.reqWaladomPhotoRepository = reqWaladomPhotoRepository;
        this.reqIdProofRepository = reqIdProofRepository;
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
        userManagementService.delete(requestId, registrationRequestRepository);
    }

    @Transactional
    public List<UserResponseDTO> getAllRegistrationRequests() {
        return userManagementService.findAll(registrationRequestRepository)
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
                    // Map WaladomCardPhoto
                    ReqWaladomPhoto waladomCard = user.getReqWaladomPhoto();
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

                    // Map WaladomCardPhoto
                    ReqWaladomPhoto waladomCard = user.getReqWaladomPhoto();
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

    private User saveUser(UserRequestDTO userRequest) {
        User newUser = new User();
        // Map User fields from DTO
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
        ReqWaladomPhoto reqWaladomIdPhoto = new ReqWaladomPhoto();
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


}
