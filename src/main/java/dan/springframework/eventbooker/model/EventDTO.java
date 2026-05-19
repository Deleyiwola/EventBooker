package dan.springframework.eventbooker.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventDTO {
    private Long id;
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
