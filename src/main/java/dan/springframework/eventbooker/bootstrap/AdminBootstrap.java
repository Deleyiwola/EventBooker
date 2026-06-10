package dan.springframework.eventbooker.bootstrap;

import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin(){

        if(userRepository.findByEmail("oladevopsltd@gmail.com").isEmpty()){

            User admin = User.builder()
                    .name("System Admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("oladevopsltd@gmail.com")
                    .phoneNumber("09058663246")
                    .role(Role.ROLE_ADMIN)
                    .build();

            userRepository.save(admin);
        }

    }
}
