package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.EventDTO;
import dan.springframework.eventbooker.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EventController {
    public static final String EVENT_URI = "/bookingApi/v1/events";
    public static final String EVENT_ID_URI = "/bookingApi/v1/events/{eventId}";

    private final EventService eventService;

    @GetMapping(EVENT_URI)
    public List<EventDTO> listEvents() {
        return eventService.listEvents();
    }

    @GetMapping(EVENT_ID_URI)
    public EventDTO getEventById(@PathVariable UUID eventId) {

        log.info("Getting event with id {}", eventId);

        return eventService.getEventById(eventId);
    }

    @PostMapping(EVENT_URI)
    public ResponseEntity<EventDTO> createNewEvent(@RequestBody EventDTO eventDTO) {

        EventDTO savedEvent = eventService.createEvent(eventDTO);

        return ResponseEntity
                .created(URI.create(EVENT_URI + "/" + savedEvent.getId()))
                .body(savedEvent);
    }

    @PutMapping(EVENT_ID_URI)
    public ResponseEntity<EventDTO> updateEvent(@PathVariable UUID eventId, @RequestBody EventDTO eventDTO) {
        return eventService.updateEvent(eventId, eventDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @PatchMapping(EVENT_ID_URI)
    public ResponseEntity<EventDTO> patchEvent(@PathVariable UUID eventId, @RequestBody EventDTO eventDTO) {
        return eventService.patchEvent(eventId, eventDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @DeleteMapping(EVENT_ID_URI)
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId) {
        eventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }
}
