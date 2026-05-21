package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.model.LoginRequest;
import dan.springframework.eventbooker.model.RegisterUser;
import dan.springframework.eventbooker.model.UserDTO;
import dan.springframework.eventbooker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bookingApi/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterUser registerUser) {
        String token = authService.register(registerUser);

        return Map.of("token", token, "type", "Bearer");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        String token = authService.login(
                request.getEmail(),
                request.getPassword());

        return Map.of("token", token, "type", "Bearer");
    }

    @GetMapping("/my-profile")
    public UserDTO getMyProfile(Authentication authentication) {
        return authService.getProfile(authentication.getName());
    }
}
