package dan.springframework.eventbooker.model;

public record CreateBookingRequest(Long eventId,
                                   Integer numberOfSeatsBooked) {

}
