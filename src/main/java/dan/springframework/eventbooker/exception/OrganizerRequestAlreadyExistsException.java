package dan.springframework.eventbooker.exception;

public class OrganizerRequestAlreadyExistsException extends RuntimeException {
    public OrganizerRequestAlreadyExistsException(String message) {
        super(message);
    }
}
