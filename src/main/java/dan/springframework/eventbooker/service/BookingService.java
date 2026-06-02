package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.model.BookingDTO;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(CreateBookingRequest request, String email);

    BookingDTO getBookingById(Long id, String email);

    List<BookingDTO> getBookings();

    List<BookingDTO> getBookingsByUserEmail(String email);

    List<BookingDTO> getBookingsByEventId(Long eventId);

    boolean cancelBooking(Long bookingId, String email);

    BookingDTO updateBooking(Long bookingId,BookingDTO booking, String email );

    BookingDTO patchBooking(Long bookingId, BookingDTO booking,  String email);








}
