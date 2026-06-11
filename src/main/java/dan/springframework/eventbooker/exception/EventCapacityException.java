package dan.springframework.eventbooker.exception;

public class EventCapacityException extends RuntimeException {
    public EventCapacityException(String message) {
        super(message);
    }
}
