package com.phanta.waladom.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDTO eventRequestDTO) {
        return eventService.createEvent(eventRequestDTO);
    }

    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable String eventId, @RequestBody EventRequestDTO eventRequestDTO) {
        return eventService.updateEvent(eventId, eventRequestDTO);
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/get/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable String eventId) {
        return eventService.getEventById(eventId);
    }


    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEventById(@PathVariable String eventId) {
        return eventService.deleteEventById(eventId);
    }

}
