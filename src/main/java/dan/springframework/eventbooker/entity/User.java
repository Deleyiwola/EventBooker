package dan.springframework.eventbooker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String phoneNumber;
    private String name;
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    public void addBooking(Booking booking) {
        if (booking == null) {
            bookings = new HashSet<>();
        }
        bookings.add(booking);
        booking.setUser(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setUser(null);
    }
}
