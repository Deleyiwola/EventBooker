package dan.springframework.eventbooker.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {
    private UUID bookingId;

    private Long userId;
    private String userName;

    private UUID eventId;
    private String eventName;

    @NotNull
    private Integer numberOfSeatsBooked;

    private LocalDateTime timeBooked;

    private LocalDateTime lastUpdatedTime;

}
