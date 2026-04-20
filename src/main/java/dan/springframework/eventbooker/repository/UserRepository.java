package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
