package dan.springframework.eventbooker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUser {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
}
