package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.model.LoginRequest;
import dan.springframework.eventbooker.model.RegisterUser;
import dan.springframework.eventbooker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/bookingApi/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegisterUser registerUser) {
        String token = authService.register(registerUser);

        return Map.of("token", token, "type", "Bearer");
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(
                request.getEmail(),
                request.getPassword());

        return Map.of("token", token, "type", "Bearer");
    }
}
