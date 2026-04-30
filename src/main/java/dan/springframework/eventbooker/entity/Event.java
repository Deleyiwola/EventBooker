package dan.springframework.eventbooker.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

//    @NotNull
//    @NotBlank
//    @Size(max = 50)
    private String eventName;

    private Integer capacity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder.Default
    @OneToMany(mappedBy = "event", orphanRemoval = true)
    @JsonManagedReference
    private Set<Booking> bookings = new HashSet<>();

    public void addBooking(Booking booking){
        bookings.add(booking);
        booking.setEvent(this);
    }

    public void removeBooking(Booking booking){
        bookings.remove(booking);
        booking.setEvent(null);
    }
    public int getBookedSeats () {
        return bookings.stream()
            .mapToInt(Booking::getNumberOfSeatsBooked)
            .sum();
    }
    public int getAvailableSeats (){
        return capacity-getBookedSeats();
    }
}
