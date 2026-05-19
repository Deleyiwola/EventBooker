package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.entity.Event;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.mapper.BookingMapper;
import dan.springframework.eventbooker.mapper.EventMapper;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.EventDTO;
import dan.springframework.eventbooker.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final BookingMapper bookingMapper;

    @Override
    public List<EventDTO> listEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::eventToEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return eventMapper.eventToEventDTO(event);
    }

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = eventMapper.eventDTOToEvent(eventDTO);
        Event savedEvent = eventRepository.save(event);
        if (savedEvent.getCapacity()==null) {
            throw new RuntimeException("Capacity Must not be null");
        }
        return eventMapper.eventToEventDTO(savedEvent);
    }

    @Override
    public Optional<EventDTO> updateEvent(Long eventId, EventDTO event) {
        AtomicReference<Optional<EventDTO>> atomicReference = new AtomicReference<>();

        eventRepository.findById(eventId).ifPresentOrElse(existingEvent -> {
            existingEvent.setEventName(event.getEventName());
            existingEvent.setCapacity(event.getCapacity());
            existingEvent.setStartTime(event.getStartTime());
            existingEvent.setEndTime(event.getEndTime());
            atomicReference.set(Optional.of(eventMapper
                    .eventToEventDTO(eventRepository.save(existingEvent))));
        },   () ->{
            atomicReference.set(Optional.empty());
        });
        return atomicReference.get();
    }

    @Override
    public boolean deleteEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        eventRepository.delete(event);

        return true;
    }
    @Override
    public Optional<EventDTO> patchEvent(Long eventId, EventDTO event) {
        AtomicReference<Optional<EventDTO>> atomicReference = new AtomicReference<>();

        eventRepository.findById(eventId).ifPresentOrElse(existingEvent -> {
            if (StringUtils.hasText(event.getEventName())) {
                existingEvent.setEventName(event.getEventName());
            }
            if (event.getCapacity() != null) {
                existingEvent.setCapacity(event.getCapacity());
            }
            if (event.getStartTime() != null) {
                existingEvent.setStartTime(event.getStartTime());
            }
            if (event.getEndTime() != null) {
                existingEvent.setEndTime(event.getEndTime());
            }
            atomicReference.set(Optional.of(eventMapper
                    .eventToEventDTO(eventRepository.save(existingEvent))));
        },  () ->{
            atomicReference.set(Optional.empty());
        });
        return atomicReference.get();
    }

    @Override
    public List<BookingDTO> getEventBookings(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return event.getBookings()
                .stream()
                .map(bookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }
}
