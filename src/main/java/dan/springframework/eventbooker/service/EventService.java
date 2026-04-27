package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.EventDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventService {

    List<EventDTO> listEvents();

    EventDTO getEventById(UUID id);

    EventDTO createEvent(EventDTO event);

    Optional<EventDTO> updateEvent(UUID eventId, EventDTO event);

    boolean deleteEvent(UUID id);

    Optional<EventDTO> patchEvent(UUID eventId, EventDTO event);

    List<BookingDTO> getEventBookings(UUID eventId);


}
