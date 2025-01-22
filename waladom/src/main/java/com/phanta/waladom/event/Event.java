package com.phanta.waladom.event;

import com.phanta.waladom.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "EVENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @Column(name = "ID", length = 50, nullable = false, updatable = false)
    private String id = "EVT_" + UUID.randomUUID();

    @Column(name = "TITLE", length = 255, nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    @Column(name = "EVENT_HOUR", nullable = false)
    private LocalTime eventHour;

    @Column(name = "GOALS", columnDefinition = "TEXT")
    private String goals;

    @Column(name = "ORGANISER", length = 255)
    private String organiser;

    @Column(name = "ORGANISER_PHONE", length = 20)
    private String organiserPhone;

    @Column(name = "ORGANISER_EMAIL", length = 255)
    private String organiserEmail;

    @Column(name = "ORGANISER_WEBSITE", length = 255)
    private String organiserWebsite;

    @Column(name = "PERSON_TO_CONTACT", length = 255)
    private String personToContact;

    @Column(name = "EVENT_LOCATION", length = 255, nullable = false)
    private String eventLocation;

    @Column(name = "CAPACITY", nullable = false)
    private Integer capacity = 100;

    @Column(name = "PRICE", precision = 10, scale = 2, nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status;

    @Column(name = "EVENT_TYPE", length = 50, nullable = false)
    private String eventType;

    @Column(name = "IMAGE_URL", length = 255)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false)
    private User createdBy;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getEventHour() {
        return eventHour;
    }

    public void setEventHour(LocalTime eventHour) {
        this.eventHour = eventHour;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getOrganiserPhone() {
        return organiserPhone;
    }

    public void setOrganiserPhone(String organiserPhone) {
        this.organiserPhone = organiserPhone;
    }

    public String getOrganiserEmail() {
        return organiserEmail;
    }

    public void setOrganiserEmail(String organiserEmail) {
        this.organiserEmail = organiserEmail;
    }

    public String getOrganiserWebsite() {
        return organiserWebsite;
    }

    public void setOrganiserWebsite(String organiserWebsite) {
        this.organiserWebsite = organiserWebsite;
    }

    public String getPersonToContact() {
        return personToContact;
    }

    public void setPersonToContact(String personToContact) {
        this.personToContact = personToContact;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
