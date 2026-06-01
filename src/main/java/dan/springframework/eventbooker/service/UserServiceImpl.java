package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.mapper.BookingMapper;
import dan.springframework.eventbooker.mapper.UserMapper;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.UserDTO;
import dan.springframework.eventbooker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    @Override
    public List<UserDTO> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.userToUserDTO(user);
    }



    @Override
    public Optional<UserDTO> updateUser(UserDTO user, String email) {
        AtomicReference<Optional<UserDTO>> atomicReference = new AtomicReference<>();

        userRepository.findByEmail(email).ifPresentOrElse(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            atomicReference.set(Optional.of(userMapper
                    .userToUserDTO(userRepository.save(existingUser))));
        }, () ->{
            atomicReference.set(Optional.empty());
        });
        return atomicReference.get();
    }

    @Override
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.getBookings().clear();
        userRepository.delete(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("User not found"));

        user.getBookings().clear();
        userRepository.delete(user);
    }

    @Transactional
    @Override
    public UserDTO patchUser(UserDTO user, String email) {

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User not found"));

        if (StringUtils.hasText(user.getName())) {
            existingUser.setName(user.getName());
        }

        if (StringUtils.hasText(user.getPhoneNumber())) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }

        return userMapper.userToUserDTO(userRepository.save(existingUser));
    }

    @Override
    public List<BookingDTO> getUserBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return user.getBookings()
                .stream()
                .map(bookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User not found"));

        return userMapper.userToUserDTO(user);
    }
}
