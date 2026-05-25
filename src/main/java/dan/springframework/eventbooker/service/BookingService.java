package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDTO createBooking(CreateBookingRequest request, String email);

    BookingDTO getBookingById(Long id, String email);

    List<BookingDTO> getBookings();

    List<BookingDTO> getBookingsByUserEmail(String email);

    List<BookingDTO> getBookingsByEventId(Long eventId);

    boolean cancelBooking(Long bookingId, String email);

    Optional<BookingDTO> updateBooking(Long bookingId,BookingDTO booking, String email );

    Optional<BookingDTO> patchBooking(Long bookingId, BookingDTO booking,  String email);








}
