package dan.springframework.eventbooker.bootstrap;

import dan.springframework.eventbooker.entity.Booking;
import dan.springframework.eventbooker.entity.Event;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.repository.BookingRepository;
import dan.springframework.eventbooker.repository.EventRepository;
import dan.springframework.eventbooker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;


    @Override
    public void run(String... args) throws Exception {
        loadUserData();
        loadEventData();
        loadBookingData();

    }




    private void loadEventData() {
        if (eventRepository.count() == 0) {
            Event event1 = Event.builder()
                    .eventName("Test Event 1")
                    .capacity(400)
                    .startTime(LocalDateTime.of(2020, Month.APRIL, 20, 14, 30 ))
                    .endTime(LocalDateTime.of(2020, Month.APRIL, 20, 18, 30 ))
                    .build();

            Event event2 = Event.builder()
                    .eventName("Test Event 2")
                    .capacity(500)
                    .startTime(LocalDateTime.of(2026, Month.AUGUST , 19 , 10, 30))
                    .endTime(LocalDateTime.of(2026, Month.AUGUST , 19 , 13, 30))
                    .build();

            Event event3 = Event.builder()
                    .eventName("Test Event 3")
                    .capacity(350)
                    .startTime(LocalDateTime.of(2026, Month.NOVEMBER, 29 , 10, 30))
                    .endTime(LocalDateTime.of(2026, Month.NOVEMBER, 29 , 12, 30))
                    .build();
            eventRepository.save(event1);
            eventRepository.save(event2);
            eventRepository.save(event3);

        }
    }

    private void loadUserData() {
        if (userRepository.count() == 0) {
            User user1 = User.builder()
                    .phoneNumber("09185637281")
                    .name("John Doe")
                    .email("doeyjohn@gmail.com")
                    .build();

            User user2 = User.builder()
                    .phoneNumber("09033456871")
                    .name("Daniel James")
                    .email("jamesisdanny@gmail.com")
                    .build();

            User user3 = User.builder()
                    .phoneNumber("08134582967")
                    .name("Jenn Zoe")
                    .email("j3nnzo3@gmail.com")
                    .build();

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);


        }

    }
    private void loadBookingData() {


        List<User> users = userRepository.findAll();
        List<Event> events = eventRepository.findAll();

        if (bookingRepository.count() == 0) {
            Booking booking1 = Booking.builder()
                    .user(users.get(0))
                    .event(events.get(0))
                    .timeBooked(LocalDateTime.of(2026, 3, 30, 10, 30))
                    .lastUpdatedTime(LocalDateTime.of(2026, 3, 30, 10, 30))
                    .numberOfSeatsBooked(390)
                    .build();


            Booking booking2 = Booking.builder()
                    .user(users.get(1))
                    .event(events.get(1))
                    .lastUpdatedTime(LocalDateTime.of(2026, 4, 15, 10, 30))
                    .timeBooked(LocalDateTime.of(2026, 4, 15, 10, 30))
                    .numberOfSeatsBooked(450)
                    .build();


            Booking booking3 = Booking.builder()
                    .user(users.get(2))
                    .event(events.get(1))
                    .timeBooked(LocalDateTime.of(2026, 4, 1, 10, 30))
                    .lastUpdatedTime(LocalDateTime.of(2026, 4, 1, 10, 30))
                    .numberOfSeatsBooked(39)
                    .build();


            bookingRepository.save(booking1);
            bookingRepository.save(booking2);
            bookingRepository.save(booking3);







        }
    }
}
