package com.phanta.waladom.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventResponseDTO {
    private String id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime eventHour;
    private String goals;
    private String organiser;
    private String organiserPhone;
    private String organiserEmail;
    private String personToContact;
    private String organiserWebsite;
    private String location;
    private int capacity;
    private BigDecimal price;
    private String status;
    private String eventType;
    private String imageUrl;
    private String createdBy;



    public EventResponseDTO(){

    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getOrganiserWebsite() {
        return organiserWebsite;
    }

    public void setOrganiserWebsite(String organiserWebsite) {
        this.organiserWebsite = organiserWebsite;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPersonToContact() {
        return personToContact;
    }

    public void setPersonToContact(String personToContact) {
        this.personToContact = personToContact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
