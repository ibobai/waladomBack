package com.phanta.waladom.report;


import com.phanta.waladom.report.evidence.ReportEvidence;
import com.phanta.waladom.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @Column(name = "id", length = 50, nullable = false, unique = true)
    private String id = "RPT_" + UUID.randomUUID();

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "country", length = 100, nullable = false)
    private String country;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "actor", length = 100)
    private String actor = "gov";

    @Column(name = "actor_name", length = 100)
    private String actorName;

    @Column(name = "actor_desc", length = 100)
    private String actorDesc;

    @Column(name = "actor_account", length = 250)
    private String actorAccount;

    @Column(name = "victim", length = 10)
    private String victim = "others";

    @Column(name = "google_map_link", columnDefinition = "TEXT")
    private String googleMapLink;

    @Column(name = "status", length = 20)
    private String status = "not verified";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt= LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt= LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading to avoid unnecessary joins
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReportEvidence> evidenceList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ReportEvidence> getEvidenceList() {
        return evidenceList;
    }

    public void setEvidenceList(List<ReportEvidence> evidenceList) {
        this.evidenceList = evidenceList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}


