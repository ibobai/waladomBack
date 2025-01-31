package com.phanta.waladom.report;


import com.phanta.waladom.report.evidence.ReportEvidenceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class ReportRequestDTO {
    private String userId;
    private String type;
    private String description;
    private String country;
    private String city;
    private String actor;
    private String actorName;
    private String actorDesc;
    private String actorAccount;
    private String victim;
    private String googleMapLink;
    private String status;
    private String verifierComment;
    private Boolean verified;
    private List<ReportEvidenceDTO> evidenceList;

    public ReportRequestDTO() {
    }

    public String getVerifierComment() {
        return verifierComment;
    }

    public void setVerifierComment(String verifierComment) {
        this.verifierComment = verifierComment;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorDesc() {
        return actorDesc;
    }

    public void setActorDesc(String actorDesc) {
        this.actorDesc = actorDesc;
    }

    public String getActorAccount() {
        return actorAccount;
    }

    public void setActorAccount(String actorAccount) {
        this.actorAccount = actorAccount;
    }



    public String getGoogleMapLink() {
        return googleMapLink;
    }

    public void setGoogleMapLink(String googleMapLink) {
        this.googleMapLink = googleMapLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getVictim() {
        return victim;
    }

    public void setVictim(String victim) {
        this.victim = victim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ReportEvidenceDTO> getEvidenceList() {
        return evidenceList;
    }

    public void setEvidenceList(List<ReportEvidenceDTO> evidenceList) {
        this.evidenceList = evidenceList;
    }
}
