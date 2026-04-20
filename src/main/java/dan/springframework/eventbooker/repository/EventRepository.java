package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
