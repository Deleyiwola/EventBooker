package dan.springframework.eventbooker.repository;

import dan.springframework.eventbooker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}
