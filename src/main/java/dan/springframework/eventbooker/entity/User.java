package dan.springframework.eventbooker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

//    @NotNull
//    @NotBlank
    private String name;

//    @NotNull
//    @NotBlank
    private String email;

    @JsonIgnore
    private String password;

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
