package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Email(String email);
    List<Booking> findByEventIdAndUser_Email(Long eventId, String email);
    Optional<Booking> findByBookingIdAndUser_Email(Long bookingId, String email);
}
