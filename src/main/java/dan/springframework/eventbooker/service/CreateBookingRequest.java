package dan.springframework.eventbooker.service;

import java.util.UUID;

public record CreateBookingRequest(Long userId, Long eventId, Integer numberOfSeatsBooked) {

}
