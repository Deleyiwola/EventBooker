package dan.springframework.eventbooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

   @NotNull
   @NotBlank
   @Size(max = 20)
   @Column(length = 20)
    private String phoneNumber;

    @NotNull
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
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
