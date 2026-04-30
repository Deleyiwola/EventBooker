package dan.springframework.eventbooker.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //   @NotNull
//   @NotBlank
    private String phoneNumber;

    @Column(length = 255)
    private String userName;

//    @NotNull
//    @NotBlank
    private String name;

//    @NotNull
//    @NotBlank

    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonManagedReference
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
