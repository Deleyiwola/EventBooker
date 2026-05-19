package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
