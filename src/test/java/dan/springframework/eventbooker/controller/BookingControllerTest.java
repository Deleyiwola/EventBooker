package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.config.CustomUserDetailService;
import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.CreateBookingRequest;
import dan.springframework.eventbooker.security.JwtAuthenticationFilter;
import dan.springframework.eventbooker.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonMapper jsonMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    private UsernamePasswordAuthenticationToken userToken(User user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    private UsernamePasswordAuthenticationToken organiserToken(User user){
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

    }
    private UsernamePasswordAuthenticationToken adminToken(User user){
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
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
                                .principal(userToken(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1));

        verify(bookingService).getBookingById(1L,"user@test.com");
    }

    @Test
    void getMyBookingByUserEmail() throws Exception {
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
                                .principal(userToken(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(1));

        verify(bookingService).getBookingsByUserEmail("user@test.com");

    }

    @Test
    void createBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        CreateBookingRequest request =
                new CreateBookingRequest(10L,2);

        BookingDTO bookingDTO = BookingDTO.builder()
                .bookingId(1L)
                .eventId(10L)
                .numberOfSeatsBooked(2)
                .timeBooked(LocalDateTime.now())
                .build();

        when(bookingService.createBooking(any(),eq("user@test.com")))
                .thenReturn(bookingDTO);

        mockMvc.perform(post("/bookingApi/v1/bookings")
                        .principal(userToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.eventId").value(10))
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(2));

        verify(bookingService).createBooking(any(CreateBookingRequest.class),eq("user@test.com"));
    }

    @Test
    void updateBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO savedBooking = BookingDTO.builder()
                .eventId(10L)
                .numberOfSeatsBooked(3)
                .build();

        BookingDTO updateBooking = BookingDTO.builder()
                .bookingId(1L)
                .eventId(10L)
                .numberOfSeatsBooked(3)
                .build();

        when(bookingService.updateBooking(eq(1L), any(), eq("user@test.com")))
                .thenReturn(updateBooking);

        mockMvc.perform(put("/bookingApi/v1/bookings/1")
                        .principal(userToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(savedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(3));

        verify(bookingService).updateBooking(eq(1L),any(),eq("user@test.com"));
    }

    @Test
    void patchBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO savedBooking = BookingDTO.builder()
                .numberOfSeatsBooked(5)
                .build();

        BookingDTO updateBooking = BookingDTO.builder()
                .bookingId(1L)
                .numberOfSeatsBooked(5)
                .build();

        when(bookingService.patchBooking(eq(1L),any(),eq("user@test.com")))
                .thenReturn(updateBooking);

        mockMvc.perform(patch("/bookingApi/v1/bookings/1")
                        .principal(userToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(savedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(5));

        verify(bookingService).patchBooking(eq(1L),any(),eq("user@test.com"));
    }

    @Test
    void deleteBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(bookingService.cancelBooking(eq(1L),eq("user@test.com")))
                .thenReturn(true);

        mockMvc.perform(delete("/bookingApi/v1/bookings/1")
                        .principal(userToken(user)))
                .andExpect(status().isNoContent());

        verify(bookingService).cancelBooking(eq(1L),eq("user@test.com"));
    }

    @Test
    void getBookingByEventId() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .bookingId(1L)
                .eventId(20L)
                .build();

        when(bookingService.getBookingsByEventId(eq(20L), eq("organizer@test.com")))
                .thenReturn(List.of(bookingDTO));

        mockMvc.perform(get("/bookingApi/v1/bookings/events/20")
                        .principal(organiserToken(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(20));

        verify(bookingService).getBookingsByEventId(eq(20L), eq("organizer@test.com"));
    }

    @Test
    void listBookings() throws Exception {


        User user = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(1L);

        when(bookingService.getBookings())
                .thenReturn(List.of(bookingDTO));

        mockMvc.perform(
                        get("/bookingApi/v1/bookings")
                                .principal(adminToken(user))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(1L));

        verify(bookingService).getBookings();

    }

}