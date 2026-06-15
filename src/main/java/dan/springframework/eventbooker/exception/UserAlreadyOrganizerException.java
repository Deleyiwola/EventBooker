package dan.springframework.eventbooker.exception;

public class UserAlreadyOrganizerException extends RuntimeException {
    public UserAlreadyOrganizerException(String message) {
        super(message);
    }
}
