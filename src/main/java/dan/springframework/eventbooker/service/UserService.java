package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> listUsers();

    UserDTO getUserById(Long id);

    Optional<UserDTO> updateUser(UserDTO user, String email);

    void deleteUser(String email);

    void deleteUserById(Long id);

    UserDTO patchUser(UserDTO user, String email);

    List<BookingDTO> getUserBookings(String email);

    UserDTO getProfile(String email);
}
