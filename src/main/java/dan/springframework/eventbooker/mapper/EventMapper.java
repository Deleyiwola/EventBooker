package dan.springframework.eventbooker.mapper;

import dan.springframework.eventbooker.entity.Event;
import dan.springframework.eventbooker.model.EventDTO;
import org.mapstruct.*;

@Mapper

public interface EventMapper {
    // Event->DTO
    @Mapping(target = "bookedSeats", expression = "java(event.getBookedSeats())")
    EventDTO eventToEventDTO(Event event);

  // DTO -> Event
    @Mapping(target ="bookings", ignore = true )
    Event eventDTOToEvent(EventDTO dto);
}