package com.phanta.waladom.utiles;

import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.registration.RegistrationRequest;
import com.phanta.waladom.registration.photos.reqIdPhoto.ReqWaladomPhoto;
import com.phanta.waladom.registration.photos.reqIdProof.ReqIdProof;
import com.phanta.waladom.role.Role;
import com.phanta.waladom.user.User;

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
        user.setId(registrationRequest.getId());
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
        user.setMaritalStatus(registrationRequest.getMaritalStatus());
        user.setNumberOfKids(registrationRequest.getNumberOfKids());
        user.setOccupation(registrationRequest.getOccupation());
        user.setSex(registrationRequest.getSex());
        user.setMothersFirstName(registrationRequest.getMothersFirstName());
        user.setMothersLastName(registrationRequest.getMothersLastName());
        user.setNationalities(registrationRequest.getNationalities());
        user.setComments(registrationRequest.getComments());

        // Assuming photos are available in RegistrationRequest
        if (registrationRequest.getReqWaladomPhoto() != null) {
            WaladomIdPhoto waladomPhoto = new WaladomIdPhoto();
            waladomPhoto.setId(registrationRequest.getReqWaladomPhoto().getId());
            waladomPhoto.setPhotoUrl(registrationRequest.getReqWaladomPhoto().getPhotoUrl());
            waladomPhoto.setCreatedAt(registrationRequest.getCreatedAt());
            waladomPhoto.setUpdatedAt(registrationRequest.getUpdatedAt());
            user.setWaladomIdPhoto(waladomPhoto);

        }

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
            ReqWaladomPhoto waladomPhoto = new ReqWaladomPhoto();
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


}
