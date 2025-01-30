package com.phanta.waladom.event;

import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(EventController.class);


    @Transactional
    public ResponseEntity<?> createEvent(EventRequestDTO eventRequestDTO) {
        log.info("Starting event creation process for title: {}", eventRequestDTO.getTitle());

        // Map DTO to Entity
        Event event = new Event();
        Optional<User> existingUser = userRepository.findById(eventRequestDTO.getCreatedBy());
        if (existingUser.isEmpty()) {
            log.warn("User not found with ID: {}", eventRequestDTO.getCreatedBy());
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "User not found",
                            "message", "No user found with this ID : " + eventRequestDTO.getCreatedBy(),
                            "path", "/api/event/create"
                    )
            );
        }

        event.setTitle(eventRequestDTO.getTitle());
        event.setDescription(eventRequestDTO.getDescription());
        event.setDate(eventRequestDTO.getDate());
        event.setEventHour(eventRequestDTO.getEventHour());
        event.setGoals(eventRequestDTO.getGoals());
        event.setOrganiser(eventRequestDTO.getOrganiser());
        event.setOrganiserPhone(eventRequestDTO.getOrganiserPhone());
        event.setOrganiserEmail(eventRequestDTO.getOrganiserEmail());
        event.setPersonToContact(eventRequestDTO.getPersonToContact());
        event.setEventLocation(eventRequestDTO.getEventLocation());
        event.setCapacity(eventRequestDTO.getCapacity());
        event.setPrice(eventRequestDTO.getPrice());
        event.setStatus(eventRequestDTO.getStatus());
        event.setEventType(eventRequestDTO.getEventType());
        event.setImageUrl(eventRequestDTO.getImageUrl());
        event.setOrganiserWebsite(eventRequestDTO.getOrganiserWebsite());
        event.setCreatedBy(existingUser.get());

        // Save to database
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with ID: {}", savedEvent.getId());

        // Prepare response DTO and set values manually
        EventResponseDTO responseDTO = new EventResponseDTO();
        responseDTO.setId(savedEvent.getId());
        responseDTO.setTitle(savedEvent.getTitle());
        responseDTO.setDescription(savedEvent.getDescription());
        responseDTO.setDate(savedEvent.getDate());
        responseDTO.setEventHour(savedEvent.getEventHour());
        responseDTO.setGoals(savedEvent.getGoals());
        responseDTO.setOrganiser(savedEvent.getOrganiser());
        responseDTO.setOrganiserPhone(savedEvent.getOrganiserPhone());
        responseDTO.setOrganiserEmail(savedEvent.getOrganiserEmail());
        responseDTO.setPersonToContact(savedEvent.getPersonToContact());
        responseDTO.setLocation(savedEvent.getEventLocation());
        responseDTO.setCapacity(savedEvent.getCapacity());
        responseDTO.setPrice(savedEvent.getPrice());
        responseDTO.setStatus(savedEvent.getStatus());
        responseDTO.setEventType(savedEvent.getEventType());
        responseDTO.setImageUrl(savedEvent.getImageUrl());
        responseDTO.setCreatedBy(savedEvent.getCreatedBy().getId());
        responseDTO.setOrganiserWebsite(savedEvent.getOrganiserWebsite());

        log.info("Event creation process completed successfully for event ID: {}", savedEvent.getId());
        return ResponseEntity.ok().body(responseDTO);
    }



    @Transactional
    public ResponseEntity<?> updateEvent(String eventId, EventRequestDTO eventRequestDTO) {
        log.info("Starting event update process for event ID: {}", eventId);

        Optional<Event> existingEventOpt = eventRepository.findById(eventId);
        if (existingEventOpt.isEmpty()) {
            log.warn("Event not found with ID: {}", eventId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Event not found",
                            "message", "No event found with ID: " + eventId,
                            "path", "/api/event/update/" + eventId
                    )
            );
        }

        Event event = existingEventOpt.get();
        log.info("Updating event with ID: {}", eventId);

        // Update fields only if they are not null
        if (eventRequestDTO.getTitle() != null) event.setTitle(eventRequestDTO.getTitle());
        if (eventRequestDTO.getDescription() != null) event.setDescription(eventRequestDTO.getDescription());
        if (eventRequestDTO.getDate() != null) event.setDate(eventRequestDTO.getDate());
        if (eventRequestDTO.getEventHour() != null) event.setEventHour(eventRequestDTO.getEventHour());
        if (eventRequestDTO.getGoals() != null) event.setGoals(eventRequestDTO.getGoals());
        if (eventRequestDTO.getOrganiser() != null) event.setOrganiser(eventRequestDTO.getOrganiser());
        if (eventRequestDTO.getOrganiserPhone() != null) event.setOrganiserPhone(eventRequestDTO.getOrganiserPhone());
        if (eventRequestDTO.getOrganiserEmail() != null) event.setOrganiserEmail(eventRequestDTO.getOrganiserEmail());
        if (eventRequestDTO.getPersonToContact() != null) event.setPersonToContact(eventRequestDTO.getPersonToContact());
        if (eventRequestDTO.getEventLocation() != null) event.setEventLocation(eventRequestDTO.getEventLocation());
        if (eventRequestDTO.getCapacity() > 0) event.setCapacity(eventRequestDTO.getCapacity());
        if (eventRequestDTO.getPrice() != null) event.setPrice(eventRequestDTO.getPrice());
        if (eventRequestDTO.getStatus() != null) event.setStatus(eventRequestDTO.getStatus());
        if (eventRequestDTO.getEventType() != null) event.setEventType(eventRequestDTO.getEventType());
        if (eventRequestDTO.getImageUrl() != null) event.setImageUrl(eventRequestDTO.getImageUrl());
        if (eventRequestDTO.getOrganiserWebsite() != null) event.setOrganiserWebsite(eventRequestDTO.getOrganiserWebsite());

        // Save updated event
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully with ID: {}", updatedEvent.getId());

        // Prepare response DTO and set values manually
        EventResponseDTO responseDTO = new EventResponseDTO();
        responseDTO.setId(updatedEvent.getId());
        responseDTO.setTitle(updatedEvent.getTitle());
        responseDTO.setDescription(updatedEvent.getDescription());
        responseDTO.setDate(updatedEvent.getDate());
        responseDTO.setEventHour(updatedEvent.getEventHour());
        responseDTO.setGoals(updatedEvent.getGoals());
        responseDTO.setOrganiser(updatedEvent.getOrganiser());
        responseDTO.setOrganiserPhone(updatedEvent.getOrganiserPhone());
        responseDTO.setOrganiserEmail(updatedEvent.getOrganiserEmail());
        responseDTO.setPersonToContact(updatedEvent.getPersonToContact());
        responseDTO.setLocation(updatedEvent.getEventLocation());
        responseDTO.setCapacity(updatedEvent.getCapacity());
        responseDTO.setPrice(updatedEvent.getPrice());
        responseDTO.setStatus(updatedEvent.getStatus());
        responseDTO.setEventType(updatedEvent.getEventType());
        responseDTO.setImageUrl(updatedEvent.getImageUrl());
        responseDTO.setOrganiserWebsite(updatedEvent.getOrganiserWebsite());
        responseDTO.setCreatedBy(updatedEvent.getCreatedBy().getId());

        log.info("Event update process completed for event ID: {}", updatedEvent.getId());
        return ResponseEntity.ok(responseDTO);
    }



    public ResponseEntity<?> getAllEvents() {
        log.info("Fetching all events...");

        List<Event> events = eventRepository.findAll();
        log.info("Retrieved {} events from the database.", events.size());

        List<EventResponseDTO> eventResponseDTOs = events.stream().map(event -> {
            EventResponseDTO responseDTO = new EventResponseDTO();
            responseDTO.setId(event.getId());
            responseDTO.setTitle(event.getTitle());
            responseDTO.setDescription(event.getDescription());
            responseDTO.setDate(event.getDate());
            responseDTO.setEventHour(event.getEventHour());
            responseDTO.setGoals(event.getGoals());
            responseDTO.setOrganiser(event.getOrganiser());
            responseDTO.setOrganiserPhone(event.getOrganiserPhone());
            responseDTO.setOrganiserEmail(event.getOrganiserEmail());
            responseDTO.setPersonToContact(event.getPersonToContact());
            responseDTO.setLocation(event.getEventLocation());
            responseDTO.setCapacity(event.getCapacity());
            responseDTO.setPrice(event.getPrice());
            responseDTO.setStatus(event.getStatus());
            responseDTO.setEventType(event.getEventType());
            responseDTO.setImageUrl(event.getImageUrl());
            responseDTO.setOrganiserWebsite(event.getOrganiserWebsite());
            responseDTO.setCreatedBy(event.getCreatedBy().getId());
            return responseDTO;
        }).collect(Collectors.toList());

        log.info("Successfully mapped {} events to DTOs.", eventResponseDTOs.size());
        return ResponseEntity.ok(eventResponseDTOs);
    }

    public ResponseEntity<?> getEventById(String eventId) {
        log.info("Attempting to retrieve event with ID: {}", eventId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if (eventOpt.isEmpty()) {
            log.warn("Event with ID: {} not found.", eventId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Event not found",
                            "message", "No event found with ID: " + eventId,
                            "path", "/api/event/get/" + eventId
                    )
            );
        }

        Event event = eventOpt.get();
        EventResponseDTO responseDTO = new EventResponseDTO();
        responseDTO.setId(event.getId());
        responseDTO.setTitle(event.getTitle());
        responseDTO.setDescription(event.getDescription());
        responseDTO.setDate(event.getDate());
        responseDTO.setEventHour(event.getEventHour());
        responseDTO.setGoals(event.getGoals());
        responseDTO.setOrganiser(event.getOrganiser());
        responseDTO.setOrganiserPhone(event.getOrganiserPhone());
        responseDTO.setOrganiserEmail(event.getOrganiserEmail());
        responseDTO.setPersonToContact(event.getPersonToContact());
        responseDTO.setLocation(event.getEventLocation());
        responseDTO.setCapacity(event.getCapacity());
        responseDTO.setPrice(event.getPrice());
        responseDTO.setOrganiserWebsite(responseDTO.getOrganiserWebsite());
        responseDTO.setStatus(event.getStatus());
        responseDTO.setEventType(event.getEventType());
        responseDTO.setImageUrl(event.getImageUrl());
        responseDTO.setOrganiserWebsite(event.getOrganiserWebsite());
        responseDTO.setCreatedBy(event.getCreatedBy().getId());

        log.info("Successfully retrieved event with ID: {}", eventId);

        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public ResponseEntity<?> deleteEventById(String eventId) {
        log.info("Attempting to delete event with ID: {}", eventId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if (eventOpt.isEmpty()) {
            log.warn("Event with ID: {} not found for deletion.", eventId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Event not found",
                            "message", "No event found with ID: " + eventId,
                            "path", "/api/event/delete/" + eventId
                    )
            );
        }

        eventRepository.deleteById(eventId);
        log.info("Event with ID: {} deleted successfully.", eventId);

        return ResponseEntity.noContent().build();
    }



}