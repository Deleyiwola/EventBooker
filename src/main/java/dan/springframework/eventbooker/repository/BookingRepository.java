package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
