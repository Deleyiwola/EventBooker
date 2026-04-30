package dan.springframework.eventbooker.service;

public record CreateBookingRequest(Long userId, Long eventId, Integer numberOfSeatsBooked) {

}
