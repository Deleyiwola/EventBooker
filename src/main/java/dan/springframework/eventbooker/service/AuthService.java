package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
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

    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return jwtService.generateToken(savedUser.getEmail());
    }

    public String login(String email, String password) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(()-> new NotFoundException("User not found"));
        System.out.println(user.getPassword());
    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new RuntimeException("Invalid Credentials");
    }

    return jwtService.generateToken(user.getEmail());
    }

}
