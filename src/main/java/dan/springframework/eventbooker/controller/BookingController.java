package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.service.BookingService;
import dan.springframework.eventbooker.service.CreateBookingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BookingController {
    public static final String BOOKING_URI = "/bookingApi/v1/bookings";
    public static final String BOOKING_ID_URI = "/bookingApi/v1/bookings/{bookingId}";
    public static final String BOOKING_USER_ID_URI = "/bookingApi/v1/bookings/users/{userId}";
    public static final String BOOKING_EVENT_ID_URI = "/bookingApi/v1/bookings/events/{eventId}";

    private final BookingService bookingService;

    @GetMapping(BOOKING_URI)
    public List<BookingDTO> listBookings() {
        return bookingService.getBookings();
    }

    @GetMapping(BOOKING_ID_URI)
    public BookingDTO getBookingById(@PathVariable UUID bookingId) {

        log.info("Getting booking with id {}", bookingId);

        return bookingService.getBookingById(bookingId);
    }

    @GetMapping(BOOKING_USER_ID_URI)
    public List<BookingDTO> getBookingByUserId(@PathVariable UUID userId) {
        log.info("Getting booking with userId {}", userId);
        return bookingService.getBookingsByUserId(userId);
    }

    @GetMapping(BOOKING_EVENT_ID_URI)
    public List<BookingDTO> getBookingsByEventId(@PathVariable UUID eventId) {
        log.info("Getting booking with eventId {}", eventId);
        return bookingService.getBookingsByEventId(eventId);
    }

    @PostMapping(BOOKING_URI)
    public ResponseEntity<BookingDTO> createNewBooking(@RequestBody CreateBookingRequest bookingRequest) {

        BookingDTO savedBooking = bookingService.createBooking(bookingRequest);

        return ResponseEntity
                .created(URI.create(BOOKING_URI + "/" + savedBooking.getBookingId()))
                .body(savedBooking);
    }

    @PutMapping(BOOKING_ID_URI)
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable UUID bookingId, @RequestBody BookingDTO bookingDTO) {
        return bookingService.updateBooking(bookingId, bookingDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @PatchMapping(BOOKING_ID_URI)
    public ResponseEntity<BookingDTO> patchBooking(@PathVariable UUID bookingId, @RequestBody BookingDTO bookingDTO) {
        return bookingService.patchBooking(bookingId, bookingDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @DeleteMapping(BOOKING_ID_URI)
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
