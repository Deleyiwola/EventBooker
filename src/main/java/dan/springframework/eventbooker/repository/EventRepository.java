package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
