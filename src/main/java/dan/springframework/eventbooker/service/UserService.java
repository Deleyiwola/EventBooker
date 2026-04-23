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

    UserDTO getUserById(Long id);

    UserDTO createUser(UserDTO user);

    Optional<UserDTO> updateUser(Long userId, UserDTO user);

    boolean deleteUser(Long id);

    Optional<UserDTO> patchUser(Long userId, UserDTO user);

    List<BookingDTO> getUserBookings(Long userId);
}
