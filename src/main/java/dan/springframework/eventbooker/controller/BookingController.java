package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.service.BookingService;
import dan.springframework.eventbooker.service.CreateBookingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('USER')")
public class BookingController {
    public static final String BOOKING_URI = "/bookingApi/v1/bookings";
    public static final String BOOKING_ID_URI = "/bookingApi/v1/bookings/{bookingId}";
    public static final String BOOKING_EVENT_ID_URI = "/bookingApi/v1/bookings/events/{eventId}";
    public static final String BOOKING_MY_BOOKING_URI = "/bookingApi/v1/bookings/my-bookings";

    private final BookingService bookingService;

    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping(BOOKING_URI)
    public List<BookingDTO> listBookings() {
        return bookingService.getBookings();
    }


    @GetMapping(BOOKING_ID_URI)
    public BookingDTO getBookingById(@PathVariable Long bookingId, Authentication authentication) {

        log.info("Getting booking with id {}", bookingId);

        return bookingService.getBookingById(bookingId, ((User)authentication.getPrincipal()).getEmail());
    }

    @GetMapping(BOOKING_MY_BOOKING_URI)
    public List<BookingDTO> getMyBookingsByUserEmail(Authentication authentication) {
        log.info("Getting booking with user {}", ((User)authentication.getPrincipal()).getEmail());
        return bookingService.getBookingsByUserEmail(((User)authentication.getPrincipal()).getEmail());
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping(BOOKING_EVENT_ID_URI)
    public List<BookingDTO> getBookingsByEventId(@PathVariable Long eventId) {
        log.info("Getting booking with eventId {}", eventId);
        return bookingService.getBookingsByEventId(eventId);
    }

    @PostMapping(BOOKING_URI)
    public ResponseEntity<BookingDTO> createNewBooking( @Validated @RequestBody CreateBookingRequest bookingRequest,
                                                        Authentication authentication) {

        BookingDTO savedBooking = bookingService.createBooking(bookingRequest, ((User)authentication.getPrincipal()).getEmail());

        return ResponseEntity
                .created(URI.create(BOOKING_URI + "/" + savedBooking.getBookingId()))
                .body(savedBooking);
    }

    @PutMapping(BOOKING_ID_URI)
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long bookingId, @Validated @RequestBody BookingDTO bookingDTO,
                                                    Authentication authentication) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, bookingDTO, ((User)authentication.getPrincipal()).getEmail()));
    }

    @PatchMapping(BOOKING_ID_URI)
    public ResponseEntity<BookingDTO> patchBooking(@PathVariable Long bookingId, @Validated @RequestBody BookingDTO bookingDTO,
                                                   Authentication authentication) {
        return ResponseEntity.ok(bookingService.patchBooking(bookingId, bookingDTO, ((User)authentication.getPrincipal()).getEmail()));
    }

    @DeleteMapping(BOOKING_ID_URI)
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.cancelBooking(bookingId, ((User)authentication.getPrincipal()).getEmail());
        return ResponseEntity.noContent().build();
    }
}
