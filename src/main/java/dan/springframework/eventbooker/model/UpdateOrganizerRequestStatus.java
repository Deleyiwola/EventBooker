package dan.springframework.eventbooker.model;

import dan.springframework.eventbooker.entity.RequestStatus;

public record UpdateOrganizerRequestStatus(
        RequestStatus status,
        String adminComment
) {
}
