package dan.springframework.eventbooker.exception;

public class UserAlreadyOrganizer extends RuntimeException {
    public UserAlreadyOrganizer(String message) {
        super(message);
    }
}
