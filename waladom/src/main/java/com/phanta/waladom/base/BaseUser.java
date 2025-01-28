package com.phanta.waladom.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@MappedSuperclass
public abstract class BaseUser {


    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 100, nullable = false)
    private String lastName;

    @Column(name = "NAME", length = 200, insertable = false, updatable = false)
    private String name; // Derived column (Full name)

    @Column(name = "EMAIL", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @Column(name = "PHONE", length = 15, nullable = false)
    private String phone;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status;

    @Column(name = "TRIBE", length = 50, nullable = false)
    private String tribe;

    @Column(name = "CURRENT_COUNTRY", length = 100, nullable = false)
    private String currentCountry;

    @Column(name = "CURRENT_CITY", length = 100, nullable = false)
    private String currentCity;

    @Column(name = "CURRENT_VILLAGE", length = 100)
    private String currentVillage;

    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    @Column(name = "BIRTH_COUNTRY", length = 100, nullable = false)
    private String birthCountry;

    @Column(name = "BIRTH_CITY", length = 100, nullable = false)
    private String birthCity;

    @Column(name = "BIRTH_VILLAGE", length = 100)
    private String birthVillage;

    @Column(name = "MARITAL_STATUS", length = 20, nullable = false)
    private String maritalStatus;

    @Column(name = "OCCUPATION", length = 20, nullable = false)
    private String occupation;

    @Column(name = "SEX", length = 1, nullable = false)
    private String sex;

    @Column(name = "CONNECTION_METHOD", length = 6, nullable = false)
    private String connectionMethod;

    @Column(name = "MOTHERS_FIRST_NAME", length = 100, nullable = false)
    private String mothersFirstName;

    @Column(name = "MOTHERS_LAST_NAME", length = 100, nullable = false)
    private String mothersLastName;

    //@ElementCollection
    //@CollectionTable(name = "USER_NATIONALITIES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "NATIONALITIES", columnDefinition = "text[]", nullable = false)
    private List<String> nationalities;


    @Column(name = "ID_CARD_ID", length = 50)
    private String idCardId;

    @Column(name = "WALADOM_CARD_ID", length = 50)
    private String waladomCardId;

    @Column(name = "COMMENTS")
    private String comments;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "NUMBER_OF_KIDS")
    private Integer numberOfKids;


    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

//    @PrePersist
//    public void generateId() {
//        if (this.id == null) {
//            this.id = getIdPrefix() + UUID.randomUUID().toString();
//        }
//    }
//



    public String getConnectionMethod() {
        return connectionMethod;
    }

    public void setConnectionMethod(String connectionMethod) {
        this.connectionMethod = connectionMethod;
    }

    protected abstract String getIdPrefix();

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIdCardId() {
        return idCardId;
    }

    public void setIdCardId(String idCardId) {
        this.idCardId = idCardId;
    }

    public String getWaladomCardId() {
        return waladomCardId;
    }

    public void setWaladomCardId(String waladomCardId) {
        this.waladomCardId = waladomCardId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getNumberOfKids() {
        return numberOfKids;
    }

    public void setNumberOfKids(Integer numberOfKids) {
        this.numberOfKids = numberOfKids;
    }
}
