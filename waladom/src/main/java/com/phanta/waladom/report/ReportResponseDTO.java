package com.phanta.waladom.report;

import com.phanta.waladom.report.evidence.ReportEvidenceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportResponseDTO {
    private String id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String verifierComment;
    private Boolean verified;
    private List<ReportEvidenceDTO> reportEvidences;

    public ReportResponseDTO(String id, String userId, String type, String description, String country, String city, String actor, String actorName, String actorDesc, String actorAccount, String victim, String googleMapLink, String status, LocalDateTime createdAt, LocalDateTime updatedAt, List<ReportEvidenceDTO> reportEvidences, String verifierComment, Boolean verified) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.country = country;
        this.city = city;
        this.actor = actor;
        this.actorName = actorName;
        this.actorDesc = actorDesc;
        this.actorAccount = actorAccount;
        this.victim = victim;
        this.googleMapLink = googleMapLink;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verified = verified;
        this.verifierComment = verifierComment;
        this.reportEvidences = reportEvidences;
    }
    public ReportResponseDTO (){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
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

    public String getVictim() {
        return victim;
    }

    public void setVictim(String victim) {
        this.victim = victim;
    }

    public String getGoogleMapLink() {
        return googleMapLink;
    }

    public void setGoogleMapLink(String googleMapLink) {
        this.googleMapLink = googleMapLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<ReportEvidenceDTO> getReportEvidences() {
        return reportEvidences;
    }

    public void setReportEvidences(List<ReportEvidenceDTO> reportEvidences) {
        this.reportEvidences = reportEvidences;
    }
}
