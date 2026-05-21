package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByEvent_Id(Long eventId);
}
