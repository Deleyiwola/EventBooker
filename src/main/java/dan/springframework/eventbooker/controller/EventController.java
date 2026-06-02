package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.EventDTO;
import dan.springframework.eventbooker.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EventController {
    public static final String EVENT_URI = "/bookingApi/v1/events";
    public static final String EVENT_ID_URI = "/bookingApi/v1/events/{eventId}";

    private final EventService eventService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping(EVENT_URI)
    public List<EventDTO> listEvents() {
        return eventService.listEvents();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(EVENT_ID_URI)
    public EventDTO getEventById(@PathVariable Long eventId) {

        log.info("Getting event with id {}", eventId);

        return eventService.getEventById(eventId);
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping(EVENT_URI)
    public ResponseEntity<EventDTO> createNewEvent(@Validated @RequestBody EventDTO eventDTO) {

        EventDTO savedEvent = eventService.createEvent(eventDTO);

        return ResponseEntity
                .created(URI.create(EVENT_URI + "/" + savedEvent.getId()))
                .body(savedEvent);
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PutMapping(EVENT_ID_URI)
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long eventId, @Validated @RequestBody EventDTO eventDTO) {
        return eventService.updateEvent(eventId, eventDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PatchMapping(EVENT_ID_URI)
    public ResponseEntity<EventDTO> patchEvent(@PathVariable Long eventId, @Validated @RequestBody EventDTO eventDTO) {
        return eventService.patchEvent(eventId, eventDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(EVENT_ID_URI)
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }
}
