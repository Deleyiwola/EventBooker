package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.mapper.UserMapper;
import dan.springframework.eventbooker.model.RegisterUser;
import dan.springframework.eventbooker.repository.UserRepository;
import dan.springframework.eventbooker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterUser registerUser) {
        if (!registerUser.getPassword().equals(registerUser.getConfirmPassword())) {
            throw new RuntimeException("The passwords don't match");
        }

        if (userRepository.findByEmail(registerUser.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = User.builder()
                .name(registerUser.getFirstName() + " " + registerUser.getLastName())
                .email(registerUser.getEmail())
                .phoneNumber(registerUser.getPhoneNumber())
                .password(passwordEncoder.encode(registerUser.getPassword()))
                .build();
        User savedUser = userRepository.save(user);

        return jwtService.generateToken(savedUser.getEmail(), savedUser.getRole());


    }

    public String login(String email, String password) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(()-> new NotFoundException("User not found"));
    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new RuntimeException("Invalid Credentials");
    }

    return jwtService.generateToken(user.getEmail(), user.getRole());
    }
}
