package dan.springframework.eventbooker.service;

import java.util.UUID;

public record CreateBookingRequest(UUID userId, UUID eventId, Integer numberOfSeatsBooked) {

}
