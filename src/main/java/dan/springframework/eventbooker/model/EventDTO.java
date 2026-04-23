package dan.springframework.eventbooker.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

//    @NotNull
//    @NotBlank
    private String eventName;

    private Integer capacity;
    private Integer bookedSeats;
    private Integer availableSeats;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
