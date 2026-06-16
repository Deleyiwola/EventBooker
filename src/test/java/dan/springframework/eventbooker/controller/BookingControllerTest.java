package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.CreateBookingRequest;
import dan.springframework.eventbooker.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private UsernamePasswordAuthenticationToken authToken(User user) {
        return new UsernamePasswordAuthenticationToken(
                    user,
            null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")) );

    }

    @Test
    void getBookingById() throws Exception {

        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(1L);

        when(bookingService.getBookingById(
                1L,
                "user@test.com"))
                .thenReturn(bookingDTO);

        mockMvc.perform(
                get("/bookingApi/v1/bookings/1")
                        .principal(authToken(user)
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1L));

        verify(bookingService).getBookingById(1L, "user@test.com");
    }

    @Test
    void getMyBookingsByUserEmail() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(1L);

        when(bookingService.getBookingsByUserEmail("user@test.com"))
                .thenReturn(List.of(bookingDTO));

        mockMvc.perform(
                get("/bookingApi/v1/bookings/my-bookings")
                        .principal(authToken(user)
                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(1L));

    }

    @Test
    void createBooking() throws Exception {

        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        CreateBookingRequest request =
                new CreateBookingRequest(10L, 2);

        BookingDTO bookingDTO = BookingDTO.builder()
                .bookingId(1L)
                .eventId(10L)
                .numberOfSeatsBooked(2)
                .timeBooked(LocalDateTime.now())
                .build();

        when(bookingService.createBooking(any(),eq("user@test.com")))
                .thenReturn(bookingDTO);

        mockMvc.perform(post("bookingApi/v1/bookings")
                .principal(authToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(1L))
                .andExpect(jsonPath("$.eventId").value(10L))
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(2));

        verify(bookingService).createBooking(any(CreateBookingRequest.class),eq("user@test.com"));
    }



}