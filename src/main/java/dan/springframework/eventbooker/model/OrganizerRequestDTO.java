package dan.springframework.eventbooker.model;


import dan.springframework.eventbooker.entity.RequestStatus;

import java.time.LocalDateTime;

public record OrganizerRequestDTO(
        Long id,
        Long userId,
        String organizationName,
        String email,
        String adminComment,
        String description,
        RequestStatus status,
        LocalDateTime requestDate,
        LocalDateTime reviewedDate
) {
}
