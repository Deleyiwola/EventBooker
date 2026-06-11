package dan.springframework.eventbooker.exception;

public class ProcessedRequestException extends RuntimeException {
    public ProcessedRequestException(String message) {
        super(message);
    }
}
