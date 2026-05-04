package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.EventDTO;

import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventDTO> listEvents();

    EventDTO getEventById(Long id);

    EventDTO createEvent(EventDTO event);

    Optional<EventDTO> updateEvent(Long eventId, EventDTO event);

    boolean deleteEvent(Long id);

    Optional<EventDTO> patchEvent(Long eventId, EventDTO event);

    List<BookingDTO> getEventBookings(Long eventId);


}
