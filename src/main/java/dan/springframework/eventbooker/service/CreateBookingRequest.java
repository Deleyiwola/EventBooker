package dan.springframework.eventbooker.service;

public record CreateBookingRequest(Long eventId, Integer numberOfSeatsBooked) {

}
