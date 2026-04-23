package dan.springframework.eventbooker.service;

import java.util.UUID;

public record CreateBookingRequest(Long userId, UUID eventId, Integer numberOfSeatsBooked) {

}
