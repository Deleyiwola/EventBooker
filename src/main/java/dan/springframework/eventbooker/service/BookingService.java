package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BookingService {
    BookingDTO createBooking(CreateBookingRequest request);

    BookingDTO getBookingById(Long id);

    List<BookingDTO> getBookings();

    List<BookingDTO> getBookingsByUserId(Long userId);

    List<BookingDTO> getBookingsByEventId(Long eventId);

    boolean cancelBooking(Long bookingId);

    Optional<BookingDTO> updateBooking(Long bookingId,BookingDTO booking);

    Optional<BookingDTO> patchBooking(Long bookingId, BookingDTO booking);








}
