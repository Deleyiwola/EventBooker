package dan.springframework.eventbooker.service;

import dan.springframework.eventbooker.exception.BookingException;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.entity.Booking;
import dan.springframework.eventbooker.entity.Event;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.mapper.BookingMapper;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.repository.BookingRepository;
import dan.springframework.eventbooker.repository.EventRepository;
import dan.springframework.eventbooker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;


    @Override
    public BookingDTO createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new NotFoundException("Event not found"));

        int requestedSeats = request.numberOfSeatsBooked();
        if (requestedSeats < 1) {
            throw new BookingException("Number of seats must be greater than zero");
        }

        int bookedSeats = event.getBookings()
                .stream()
                .mapToInt(Booking::getNumberOfSeatsBooked)
                .sum();

        int availableSeats = event.getCapacity() - bookedSeats;

        if (requestedSeats > availableSeats) {
            throw new BookingException("Not enough seats available. Seats remaining: " + availableSeats);
        }

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .numberOfSeatsBooked(requestedSeats)
                .timeBooked(LocalDateTime.now())
                .lastUpdatedTime(LocalDateTime.now())
                .build();

        user.addBooking(booking);
        event.addBooking(booking);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.bookingToBookingDTO(savedBooking);
    }

    @Override
    public BookingDTO getBookingById(UUID id) {
        Booking booking =  bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return bookingMapper.bookingToBookingDTO(booking);
    }

    @Override
    public List<BookingDTO> getBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByUserId(UUID userId) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getUser()
                        .getId().equals(userId))
                .map(bookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByEventId(UUID eventId) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getEvent()
                        .getId().equals(eventId))
                .map(bookingMapper::bookingToBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        User user = booking.getUser();
        Event event = booking.getEvent();

        user.removeBooking(booking);
        event.removeBooking(booking);
        return true;
    }

    @Override
    public Optional<BookingDTO> updateBooking(UUID bookingId, BookingDTO booking) {
        return bookingRepository.findById(bookingId).map(existingBooking ->{
            int requestedSeats = booking.getNumberOfSeatsBooked();
            if (requestedSeats < 1) {
                throw new BookingException("Number of seats must be greater than zero");
            }
            Event event = existingBooking.getEvent();

            seatConfirmation(bookingId, existingBooking, requestedSeats, event);
            existingBooking.setLastUpdatedTime(LocalDateTime.now());

            Booking updatedBooking = bookingRepository.save(existingBooking);
            return bookingMapper.bookingToBookingDTO(updatedBooking);
        });
    }

    private void seatConfirmation(UUID bookingId, Booking existingBooking, int requestedSeats, Event event) {
        int bookedSeats = event.getBookings()
                .stream()
                .filter(currentBooking -> !currentBooking.getBookingId().equals(bookingId))
                .mapToInt(Booking::getNumberOfSeatsBooked)
                .sum();

        int availableSeats = event.getCapacity() - bookedSeats;

        if (requestedSeats > availableSeats) {
            throw new BookingException("Not enough seats available. Seats remaining: " + availableSeats);
        }

        existingBooking.setNumberOfSeatsBooked(requestedSeats);
    }

    @Override
    public Optional<BookingDTO> patchBooking(UUID bookingId, BookingDTO booking) {
        return bookingRepository.findById(bookingId).map(existingBooking ->{
            Event event = existingBooking.getEvent();

            if (booking.getNumberOfSeatsBooked() != null){
                int requestedSeats = booking.getNumberOfSeatsBooked();

                if (requestedSeats < 1){
                    throw new BookingException("Number of seats must be greater than zero");
                }
                seatConfirmation(bookingId, existingBooking, requestedSeats, event);
            }
            existingBooking.setLastUpdatedTime(LocalDateTime.now());

            Booking updatedBooking = bookingRepository.save(existingBooking);

            return bookingMapper.bookingToBookingDTO(updatedBooking);
        });
    }
}
