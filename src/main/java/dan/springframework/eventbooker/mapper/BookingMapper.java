package dan.springframework.eventbooker.mapper;

import dan.springframework.eventbooker.entity.Booking;
import dan.springframework.eventbooker.model.BookingDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    // Booking -> DTO
    @Mapping(source = "lastUpdatedTime", target = "lastUpdatedTime")
    @Mapping(source = "numberOfSeatsBooked", target = "numberOfSeatsBooked")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.eventName", target = "eventName")

    BookingDTO bookingToBookingDTO(Booking booking);

    // DT0 -> Booking
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    Booking bookingDTOToBooking(BookingDTO dto);


}
