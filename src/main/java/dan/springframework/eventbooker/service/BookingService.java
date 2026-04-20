package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface BookingService {
    BookingDTO createBooking(CreateBookingRequest request);

    BookingDTO getBookingById(UUID id);

    List<BookingDTO> getBookings();

    List<BookingDTO> getBookingsByUserId(UUID userId);

    List<BookingDTO> getBookingsByEventId(UUID eventId);

    boolean cancelBooking(UUID bookingId);

    Optional<BookingDTO> updateBooking(UUID bookingId,BookingDTO booking);

    Optional<BookingDTO> patchBooking(UUID bookingId, BookingDTO booking);








}
