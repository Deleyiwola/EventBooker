package dan.springframework.eventbooker.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventDTO {
    private UUID id;
    private Integer version;
    private String eventName;
    private int capacity;
    private int bookedSeats;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
