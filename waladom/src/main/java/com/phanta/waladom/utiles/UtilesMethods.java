package com.phanta.waladom.utiles;

import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idPhoto.WaladomPhotoRepository;
import com.phanta.waladom.idProof.IdPhotoProofRepository;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.registration.RegistrationRequest;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomIdPhoto;
import com.phanta.waladom.registration.photos.reqIdProof.ReqIdProof;
import com.phanta.waladom.report.ReportRequestDTO;
import com.phanta.waladom.role.Role;
import com.phanta.waladom.shared.user.UserAndRegistrationService;
import com.phanta.waladom.shared.user.UserManagementService;
import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UtilesMethods {
    // Predefined roles
    private static final List<String> ROLE_IDS = Arrays.asList(
            "ROLE_ADMIN",
            "ROLE_CONTENT_MANAGER",
            "ROLE_MODERATOR",
            "ROLE_MEMBERSHIP_REVIEWER",
            "ROLE_USER"
    );

    @Autowired
    private final UserAndRegistrationService userAndRegistrationService;

    @Autowired
    private final UserManagementService userManagementService;
    @Autowired
    private final UserRepository userRepository;


    @Autowired
    private final WaladomPhotoRepository waladomPhotoRepository;
    @Autowired
    private final IdPhotoProofRepository idPhotoProofRepository;

    public static final List<Boolean> BOOLEANS = Arrays.asList(true, false);

    public UtilesMethods(UserAndRegistrationService userAndRegistrationService, UserManagementService userManagementService, UserRepository userRepository, WaladomPhotoRepository waladomPhotoRepository, IdPhotoProofRepository idPhotoProofRepository) {
        this.userAndRegistrationService = userAndRegistrationService;
        this.userManagementService = userManagementService;
        this.userRepository = userRepository;
        this.waladomPhotoRepository = waladomPhotoRepository;
        this.idPhotoProofRepository = idPhotoProofRepository;
    }

    /**
     * Checks if the given roleId exists in the predefined roles (case-insensitive).
     *
     * @param roleId The role ID to check.
     * @return True if the roleId exists, otherwise false.
     */
    public static boolean isRoleIdValid(String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return false;
        }
        // Check for case-insensitive match
        return ROLE_IDS.stream()
                .anyMatch(role -> role.equalsIgnoreCase(roleId));
    }



    public static User getUserFromRegistrationRequest(RegistrationRequest registrationRequest) {
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
        user.setId(registrationRequest.getId());

        if (registrationRequest.getReqIdProofPhotos() != null) {
            List<IdProofPhoto> idProofPhotos = registrationRequest.getReqIdProofPhotos().stream()
                    .map(photoDTO -> {
                        IdProofPhoto photo = new IdProofPhoto();
                        photo.setId(photoDTO.getId());
                        photo.setPhotoUrl(photoDTO.getPhotoUrl());
                        photo.setPhotoType(photoDTO.getPhotoType());
                        photo.setUpdatedAt(photoDTO.getUpdatedAt());
                        photo.setCreatedAt(photoDTO.getCreatedAt());
                        return photo;
                    })
                    .collect(Collectors.toList());
            user.setIdProofPhotos(idProofPhotos);
        }

        if (registrationRequest.getReqWaladomPhoto() != null) {
            WaladomIdPhoto waladomPhoto = new WaladomIdPhoto();

            waladomPhoto.setPhotoUrl(registrationRequest.getReqWaladomPhoto().getPhotoUrl());
            waladomPhoto.setCreatedAt(registrationRequest.getCreatedAt());
            waladomPhoto.setUpdatedAt(registrationRequest.getUpdatedAt());
            waladomPhoto.setId(registrationRequest.getReqWaladomPhoto().getId());
            user.setWaladomIdPhoto(waladomPhoto);

        }

        if(registrationRequest.getRole() != null ){
            Role role = new Role();
            role.setId(registrationRequest.getRole().getId());
            role.setName(registrationRequest.getRole().getName());
            role.setDescription(registrationRequest.getRole().getDescription());
            role.setColor(registrationRequest.getRole().getColor());
            role.setCreatedAt(registrationRequest.getRole().getCreatedAt());
            role.setUpdatedAt(registrationRequest.getRole().getUpdatedAt());
            user.setRole(role);
        }

        return user;
    }



    public static boolean isReportRequestEmpty(ReportRequestDTO request) {
        return request == null ||
                (request.getUserId() == null || request.getUserId().trim().isEmpty()) &&
                        (request.getType() == null || request.getType().trim().isEmpty()) &&
                        (request.getDescription() == null || request.getDescription().trim().isEmpty()) &&
                        (request.getCountry() == null || request.getCountry().trim().isEmpty()) &&
                        (request.getCity() == null || request.getCity().trim().isEmpty()) &&
                        (request.getActor() == null || request.getActor().trim().isEmpty()) &&
                        (request.getActorName() == null || request.getActorName().trim().isEmpty()) &&
                        (request.getActorDesc() == null || request.getActorDesc().trim().isEmpty()) &&
                        (request.getActorAccount() == null || request.getActorAccount().trim().isEmpty()) &&
                        (request.getVictim() == null || request.getVictim().trim().isEmpty()) &&
                        (request.getGoogleMapLink() == null || request.getGoogleMapLink().trim().isEmpty()) &&
                        (request.getStatus() == null || request.getStatus().trim().isEmpty()) &&
                        (request.getEvidenceList() == null || request.getEvidenceList().isEmpty());
    }

    public static boolean isReportRequestInvalid(ReportRequestDTO request) {
        return request.getType() == null || request.getType().isEmpty() ||
                request.getDescription() == null || request.getDescription().isEmpty() ||
                request.getCountry() == null || request.getCountry().isEmpty() ||
                request.getCity() == null || request.getCity().isEmpty() ||
                request.getVictim() == null || request.getVictim().isEmpty() ||
                request.getUserId() == null || request.getUserId().isEmpty();
    }


    public RegistrationRequest getRegistrationRequestFromUser(User user) {
        // Create a new RegistrationRequest object
        RegistrationRequest registrationRequest = new RegistrationRequest();

        // Set the fields from the User to the RegistrationRequest
        //registrationRequest.setUserId(user.getId());
        registrationRequest.setFirstName(user.getFirstName());
        registrationRequest.setLastName(user.getLastName());
        registrationRequest.setEmail(user.getEmail());
        registrationRequest.setPhone(user.getPhone());
        registrationRequest.setActive(user.getActive());
        registrationRequest.setStatus(user.getStatus());
        registrationRequest.setTribe(user.getTribe());
        registrationRequest.setCurrentCountry(user.getCurrentCountry());
        registrationRequest.setCurrentCity(user.getCurrentCity());
        registrationRequest.setCurrentVillage(user.getCurrentVillage());
        registrationRequest.setBirthDate(user.getBirthDate());
        registrationRequest.setBirthCountry(user.getBirthCountry());
        registrationRequest.setBirthCity(user.getBirthCity());
        registrationRequest.setBirthVillage(user.getBirthVillage());
        registrationRequest.setMaritalStatus(user.getMaritalStatus());
        registrationRequest.setNumberOfKids(user.getNumberOfKids());
        registrationRequest.setOccupation(user.getOccupation());
        registrationRequest.setSex(user.getSex());
        registrationRequest.setMothersFirstName(user.getMothersFirstName());
        registrationRequest.setMothersLastName(user.getMothersLastName());
        registrationRequest.setNationalities(user.getNationalities());
        registrationRequest.setComments(user.getComments());

        // Assuming photos are available in User
        if (user.getWaladomIdPhoto() != null) {
            ReqWaladomIdPhoto waladomPhoto = new ReqWaladomIdPhoto();
            waladomPhoto.setId(user.getWaladomIdPhoto().getId());
            waladomPhoto.setPhotoUrl(user.getWaladomIdPhoto().getPhotoUrl());
            registrationRequest.setReqWaladomPhoto(waladomPhoto);
        }

        if (user.getIdProofPhotos() != null) {
            List<ReqIdProof> idProofPhotoDTOs = user.getIdProofPhotos().stream()
                    .map(photo -> {
                        ReqIdProof reqIdProof = new ReqIdProof();
                        reqIdProof.setId(photo.getId());
                        reqIdProof.setPhotoUrl(photo.getPhotoUrl());
                        reqIdProof.setPhotoType(photo.getPhotoType());
                        return reqIdProof;
                    })
                    .collect(Collectors.toList());
            registrationRequest.setReqIdProofPhotos(idProofPhotoDTOs);
        }

        return registrationRequest;
    }



        public static String decodeUserId(String userId) {
            if (userId == null || !userId.startsWith("WLD_") || userId.length() != 15) {
                return "Invalid ID format";
            }

            // Extract gender
            char genderCode = userId.charAt(4);
            String gender = switch (genderCode) {
                case '1' -> "a male";
                case '2' -> "a female";
                default -> "other";
            };

            // Extract and format month
            String birthMonth = userId.substring(5, 7);  // Already in 2-digit format (e.g., 05, 11)

            // Extract year of birth and determine the century
            int birthYear = Integer.parseInt(userId.substring(7, 9));
            int currentYear = Year.now().getValue() % 100;
            int fullBirthYear = (birthYear > currentYear) ? 1900 + birthYear : 2000 + birthYear;

            // Extract country code
            String countryCode = userId.substring(9, 12);

            // Extract joining year and determine the full year
            int joiningYear = Integer.parseInt(userId.substring(12, 14));
            int fullJoiningYear = 2000 + joiningYear;

            // Extract the two unique random numbers at the end
            String uniqueNumbers = userId.substring(14, 16);

            return String.format("%s, born in month - %s, year %d, from country code %s, joined in %d, unique numbers: %s.",
                    gender, birthMonth, fullBirthYear, countryCode, fullJoiningYear, uniqueNumbers);
        }


    public static String getCountryCode(String countryAlpha2) {
        switch (countryAlpha2) {
            case "SUDAN": return "249";  // Sudan
            case "US": return "840";  // United States
            case "EG": return "818";  // Egypt
            case "IN": return "356";  // India
            case "FR": return "250";  // France
            case "DE": return "276";  // Germany
            case "UK": return "826";  // United Kingdom
            // Add more country codes as needed
            default:
                throw new IllegalArgumentException("Unknown country code: " + countryAlpha2);
        }
    }

}
