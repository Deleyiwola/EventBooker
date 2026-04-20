package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {

    List<UserDTO> listUsers();

    UserDTO getUserById(UUID id);

    UserDTO createUser(UserDTO user);

    Optional<UserDTO> updateUser(UUID userId, UserDTO user);

    boolean deleteUser(UUID id);

    Optional<UserDTO> patchUser(UUID userId, UserDTO user);

    List<BookingDTO> getUserBookings(UUID userId);
}
