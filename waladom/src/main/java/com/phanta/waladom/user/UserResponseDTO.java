package com.phanta.waladom.user;

import com.phanta.waladom.idPhoto.WaladomIdPhoto;
import com.phanta.waladom.idPhoto.WaladomPhotoDTO;
import com.phanta.waladom.idProof.IdProofPhoto;
import com.phanta.waladom.idProof.IdProofPhotoDTO;
import com.phanta.waladom.registration.RegistrationRequest;
import com.phanta.waladom.role.Role;
import com.phanta.waladom.role.RoleDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.phanta.waladom.utiles.UtilesMethods.getUserFromRegistrationRequest;

public class UserResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean isActive;
    private String status;
    private String tribe;
    private String currentCountry;
    private String currentCity;
    private String currentVillage;
    private LocalDate birthDate;
    private String birthCountry;
    private String birthCity;
    private String birthVillage;
    private String maritalStatus;
    private Integer numberOfKids;
    private String occupation;
    private String sex;
    private String mothersFirstName;
    private String mothersLastName;
    private List<String> nationalities;
    private String comments;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private String connectionMethod;
    private Boolean validated;

    // Photo details
    private WaladomPhotoDTO waladomCardPhoto;
    private List<IdProofPhotoDTO> idProofPhotos;
    private RoleDTO role;

    // Add constructor, getters, and setters



    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getId() {
        return id;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getConnectionMethod() {
        return connectionMethod;
    }

    public void setConnectionMethod(String connectionMethod) {
        this.connectionMethod = connectionMethod;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTribe() {
        return tribe;
    }

    public void setTribe(String tribe) {
        this.tribe = tribe;
    }

    public String getCurrentCountry() {
        return currentCountry;
    }

    public void setCurrentCountry(String currentCountry) {
        this.currentCountry = currentCountry;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getCurrentVillage() {
        return currentVillage;
    }

    public void setCurrentVillage(String currentVillage) {
        this.currentVillage = currentVillage;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthVillage() {
        return birthVillage;
    }

    public void setBirthVillage(String birthVillage) {
        this.birthVillage = birthVillage;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getNumberOfKids() {
        return numberOfKids;
    }

    public void setNumberOfKids(Integer numberOfKids) {
        this.numberOfKids = numberOfKids;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMothersFirstName() {
        return mothersFirstName;
    }

    public void setMothersFirstName(String mothersFirstName) {
        this.mothersFirstName = mothersFirstName;
    }

    public String getMothersLastName() {
        return mothersLastName;
    }

    public void setMothersLastName(String mothersLastName) {
        this.mothersLastName = mothersLastName;
    }

    public List<String> getNationalities() {
        return nationalities;
    }

    public void setNationalities(List<String> nationalities) {
        this.nationalities = nationalities;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public WaladomPhotoDTO getWaladomCardPhoto() {
        return waladomCardPhoto;
    }

    public void setWaladomCardPhoto(WaladomPhotoDTO waladomCardPhoto) {
        this.waladomCardPhoto = waladomCardPhoto;
    }

    public List<IdProofPhotoDTO> getIdProofPhotos() {
        return idProofPhotos;
    }

    public void setIdProofPhotos(List<IdProofPhotoDTO> idProofPhotos) {
        this.idProofPhotos = idProofPhotos;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public static UserResponseDTO mapToUserResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();

        responseDTO.setId(user.getId());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setActive(user.getActive());
        responseDTO.setStatus(user.getStatus());
        responseDTO.setTribe(user.getTribe());
        responseDTO.setCurrentCountry(user.getCurrentCountry());
        responseDTO.setCurrentCity(user.getCurrentCity());
        responseDTO.setCurrentVillage(user.getCurrentVillage());
        responseDTO.setBirthDate(user.getBirthDate());
        responseDTO.setBirthCountry(user.getBirthCountry());
        responseDTO.setBirthCity(user.getBirthCity());
        responseDTO.setBirthVillage(user.getBirthVillage());
        responseDTO.setMaritalStatus(user.getMaritalStatus());
        responseDTO.setNumberOfKids(user.getNumberOfKids());
        responseDTO.setOccupation(user.getOccupation());
        responseDTO.setSex(user.getSex());
        responseDTO.setMothersFirstName(user.getMothersFirstName());
        responseDTO.setMothersLastName(user.getMothersLastName());
        responseDTO.setNationalities(user.getNationalities());
        responseDTO.setComments(user.getComments());
        responseDTO.setUpdatedAt(user.getUpdatedAt());
        responseDTO.setCreatedAt(user.getCreatedAt());
        responseDTO.setConnectionMethod(user.getConnectionMethod());



        // Map Waladom photo
        if (user.getWaladomIdPhoto()  != null) {
            WaladomPhotoDTO waladomPhotoDTO = new WaladomPhotoDTO();
            WaladomIdPhoto waladomPhoto = user.getWaladomIdPhoto();
            waladomPhotoDTO.setId(waladomPhoto.getId());
            waladomPhotoDTO.setPhotoUrl(waladomPhoto.getPhotoUrl());
            waladomPhotoDTO.setCreatedAt(waladomPhoto.getCreatedAt());
            waladomPhotoDTO.setUpdatedAt(waladomPhoto.getUpdatedAt());
            responseDTO.setWaladomCardPhoto(waladomPhotoDTO);
        }

        // Map ID Proof Photos
        if (user.getIdProofPhotos() != null) {
            List<IdProofPhoto> idProofPhotos = user.getIdProofPhotos();
            List<IdProofPhotoDTO> idProofPhotoDTOs = idProofPhotos.stream()
                    .map(photo -> {
                        IdProofPhotoDTO photoDTO = new IdProofPhotoDTO();
                        photoDTO.setId(photo.getId());
                        photoDTO.setPhotoUrl(photo.getPhotoUrl());
                        photoDTO.setPhotoType(photo.getPhotoType());
                        photoDTO.setCreatedAt(photo.getCreatedAt());
                        photoDTO.setUpdatedAt(photo.getUpdatedAt());
                        return photoDTO;
                    }).collect(Collectors.toList());
            responseDTO.setIdProofPhotos(idProofPhotoDTOs);
        }

        if (user.getRole() != null) {
            RoleDTO roleDTO = new RoleDTO();
            Role role = user.getRole();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            roleDTO.setColor(role.getColor());
            roleDTO.setCreatedAt(role.getCreatedAt());
            roleDTO.setUpdatedAt(role.getUpdatedAt());
            responseDTO.setRole(roleDTO);
        }

        return responseDTO;
    }

    public static UserResponseDTO mapToRegistrationRequestResponseDTO(RegistrationRequest registrationRequest) {
        // Reuse the mapToUserResponseDTO method since it's almost identical
        UserResponseDTO responseDTO = UserResponseDTO.mapToUserResponseDTO(getUserFromRegistrationRequest(registrationRequest));

        // Set validated to false for RegistrationRequest
         responseDTO.setValidated(registrationRequest.getValidated());  // Since it's a registration request, we set validated to false
        responseDTO.setConnectionMethod(registrationRequest.getConnectionMethod());
        // Additional mapping specific to RegistrationRequest, if any
        // For example, if there are specific fields related to registration requests, you can map them here
        responseDTO.setCreatedAt(registrationRequest.getCreatedAt());
        responseDTO.setUpdatedAt(registrationRequest.getUpdatedAt());

        return responseDTO;
    }

}
