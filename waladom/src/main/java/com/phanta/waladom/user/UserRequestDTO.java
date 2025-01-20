package com.phanta.waladom.user;

import java.time.LocalDate;
import java.util.List;

public class UserRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
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
    private String idProofPhotoFront;
    private String idProofPhotoBack;
    private String waladomCardPhoto;
    private String comments;
    private String role;
    private Boolean validated;

    public UserRequestDTO() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Boolean getActive() {
        return isActive;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getIdProofPhotoFront() {
        return idProofPhotoFront;
    }

    public void setIdProofPhotoFront(String idProofPhotoFront) {
        this.idProofPhotoFront = idProofPhotoFront;
    }

    public String getIdProofPhotoBack() {
        return idProofPhotoBack;
    }

    public void setIdProofPhotoBack(String idProofPhotoBack) {
        this.idProofPhotoBack = idProofPhotoBack;
    }

    public String getWaladomCardPhoto() {
        return waladomCardPhoto;
    }

    public void setWaladomCardPhoto(String waladomCardPhoto) {
        this.waladomCardPhoto = waladomCardPhoto;
    }

    public void setNationalities(List<String> nationalities) {
        this.nationalities = nationalities;
    }

    public Integer getNumberOfKids() {
        return numberOfKids;
    }

    public void setNumberOfKids(Integer numberOfKids) {
        this.numberOfKids = numberOfKids;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
