package com.phanta.waladom.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDTO eventRequestDTO) {
        logger.info("Received request to create event: {}", eventRequestDTO);
        ResponseEntity<?> response = eventService.createEvent(eventRequestDTO);
        logger.info("Event creation response: {}", response.getStatusCode());
        return response;
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable String eventId, @RequestBody EventRequestDTO eventRequestDTO) {
        logger.info("Received request to update event with ID {}: {}", eventId, eventRequestDTO);
        ResponseEntity<?> response = eventService.updateEvent(eventId, eventRequestDTO);
        logger.info("Event update response for ID {}: {}", eventId, response.getStatusCode());
        return response;
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllEvents() {
        logger.info("Received request to fetch all events.");
        ResponseEntity<?> response = eventService.getAllEvents();
        logger.info("Fetched all events successfully.");
        return response;
    }

    @GetMapping("/get/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable String eventId) {
        logger.info("Received request to fetch event with ID {}", eventId);
        ResponseEntity<?> response = eventService.getEventById(eventId);
        logger.info("Fetched event details for ID {}: {}", eventId, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEventById(@PathVariable String eventId) {
        logger.info("Received request to delete event with ID {}", eventId);
        ResponseEntity<?> response = eventService.deleteEventById(eventId);
        logger.info("Event deletion response for ID {}: {}", eventId, response.getStatusCode());
        return response;
    }
}
