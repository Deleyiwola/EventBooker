package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.config.CustomUserDetailService;
import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.BookingDTO;
import dan.springframework.eventbooker.model.CreateBookingRequest;
import dan.springframework.eventbooker.security.JwtAuthenticationFilter;
import dan.springframework.eventbooker.service.BookingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {

        @Bean
        public RoleHierarchy roleHierarchy() {
            return RoleHierarchyImpl.withDefaultRolePrefix()
                    .role("ADMIN").implies("ORGANIZER")
                    .role("ORGANIZER").implies("USER")
                    .build();
        }

        @Bean
        public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
                RoleHierarchy roleHierarchy) {
            DefaultMethodSecurityExpressionHandler handler =
                    new DefaultMethodSecurityExpressionHandler();
            handler.setRoleHierarchy(roleHierarchy);
            return handler;
        }
    }

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

    private RequestPostProcessor auth(Authentication authentication) {
        return request -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            request.setUserPrincipal(authentication);
            return request;
        };
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private UsernamePasswordAuthenticationToken userToken(User user) {
        return new UsernamePasswordAuthenticationToken(
                user, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    private UsernamePasswordAuthenticationToken organiserToken(User user) {
        return new UsernamePasswordAuthenticationToken(
                user, null,
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );
    }
    private UsernamePasswordAuthenticationToken adminToken(User user) {
        return new UsernamePasswordAuthenticationToken(
                user, null,
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

        when(bookingService.getBookingById(1L, "user@test.com"))
                .thenReturn(bookingDTO);

        mockMvc.perform(get("/bookingApi/v1/bookings/1")
                        .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1));

        verify(bookingService).getBookingById(1L, "user@test.com");
    }

    @Test
    void getBookingById_notFound() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(bookingService.getBookingById(404L, "user@test.com"))
                .thenThrow(new NotFoundException("Booking with id '404' not found"));

        mockMvc.perform(get("/bookingApi/v1/bookings/404")
                          .with(auth(userToken(user))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Booking with id '404' not found"));

        verify(bookingService).getBookingById(404L,"user@test.com");

    }

    @Test
    void getMyBookingByUserEmail() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO bookingDTO1 = new BookingDTO();
        BookingDTO bookingDTO2 = new BookingDTO();
        BookingDTO bookingDTO3 = new BookingDTO();

        when(bookingService.getBookingsByUserEmail("user@test.com"))
                .thenReturn(List.of(bookingDTO1, bookingDTO2, bookingDTO3));

        mockMvc.perform(get("/bookingApi/v1/bookings/my-bookings")
                        .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        verify(bookingService).getBookingsByUserEmail("user@test.com");
    }

    @Test
    void getMyBookingByUserEmail_returnEmptyList() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(bookingService.getBookingsByUserEmail("user@test.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/bookingApi/v1/bookings/my-bookings")
                .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookingService).getBookingsByUserEmail("user@test.com");
    }

    @Test
    void createBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        CreateBookingRequest request = new CreateBookingRequest(10L, 2);

        BookingDTO bookingDTO = BookingDTO.builder()
                .bookingId(1L)
                .eventId(10L)
                .numberOfSeatsBooked(2)
                .timeBooked(LocalDateTime.now())
                .build();

        when(bookingService.createBooking(any(), eq("user@test.com")))
                .thenReturn(bookingDTO);

        mockMvc.perform(post("/bookingApi/v1/bookings")
                        .with(auth(userToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.eventId").value(10))
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(2));

        verify(bookingService).createBooking(any(CreateBookingRequest.class), eq("user@test.com"));
    }

    @Test
    void updateBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO updatedBooking = BookingDTO.builder()
                .eventId(10L)
                .numberOfSeatsBooked(3)
                .build();

        BookingDTO savedBooking = BookingDTO.builder()
                .bookingId(1L)
                .eventId(10L)
                .numberOfSeatsBooked(3)
                .build();

        when(bookingService.updateBooking(eq(1L), any(), eq("user@test.com")))
                .thenReturn(savedBooking);

        mockMvc.perform(put("/bookingApi/v1/bookings/1")
                        .with(auth(userToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(3));

        verify(bookingService).updateBooking(eq(1L), any(), eq("user@test.com"));
    }

    @Test
    void updateBooking_notFound() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO savedBooking = BookingDTO.builder()
                .bookingId(1L)
                .eventId(10L)
                .numberOfSeatsBooked(3)
                .build();

        when(bookingService.updateBooking(eq(404L),any(),eq("user@test.com")))
                .thenThrow(new NotFoundException("Booking with id 404 Not Found"));

        mockMvc.perform(put("/bookingApi/v1/bookings/404")
                .with(auth(userToken(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(savedBooking)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Booking with id 404 Not Found"));

        verify(bookingService).updateBooking(eq(404L),any(),eq("user@test.com"));
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

        BookingDTO updatedBooking = BookingDTO.builder()
                .bookingId(1L)
                .numberOfSeatsBooked(5)
                .build();

        when(bookingService.patchBooking(eq(1L), any(), eq("user@test.com")))
                .thenReturn(updatedBooking);

        mockMvc.perform(patch("/bookingApi/v1/bookings/1")
                        .with(auth(userToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(savedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfSeatsBooked").value(5));

        verify(bookingService).patchBooking(eq(1L), any(), eq("user@test.com"));
    }

    @Test
    void patchBooking_notFound() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        BookingDTO savedBooking = BookingDTO.builder()
                .numberOfSeatsBooked(3)
                .build();

        when(bookingService.patchBooking(eq(404L),any(),eq("user@test.com")))
                .thenThrow(new NotFoundException("Booking with id: '404' Not Found"));

        mockMvc.perform(patch("/bookingApi/v1/bookings/404")
                        .with(auth(userToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(savedBooking)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Booking with id: '404' Not Found"));

        verify(bookingService).patchBooking(eq(404L),any(),eq("user@test.com"));
    }

    @Test
    void deleteBooking() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(bookingService.cancelBooking(eq(1L), eq("user@test.com")))
                .thenReturn(true);

        mockMvc.perform(delete("/bookingApi/v1/bookings/1")
                        .with(auth(userToken(user))))
                .andExpect(status().isNoContent());

        verify(bookingService).cancelBooking(eq(1L), eq("user@test.com"));
    }

    @Test
    void deleteBooking_notFound() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(bookingService.cancelBooking(eq(404L),eq("user@test.com")))
                .thenThrow(new NotFoundException("Booking with id: '404' Not Found"));

        mockMvc.perform(delete("/bookingApi/v1/bookings/404")
                .with(auth(userToken(user))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Booking with id: '404' Not Found"));

        verify(bookingService).cancelBooking(eq(404L),eq("user@test.com"));

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
                        .with(auth(organiserToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(20));

        verify(bookingService).getBookingsByEventId(eq(20L), eq("organizer@test.com"));
    }

    @Test
    void getBookingsByEventId_forbiddenForUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/bookings/events/20")
                        .with(auth(userToken(user))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookingService);
    }

    @Test
    void listBookings() throws Exception {
        User user = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        when(bookingService.getBookings())
                .thenReturn(List.of(new BookingDTO(), new BookingDTO()));

        mockMvc.perform(get("/bookingApi/v1/bookings")
                        .with(auth(adminToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookingService).getBookings();
    }

    @Test
    void listBookings_forbiddenForUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/bookings")
                        .with(auth(userToken(user))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookingService);
    }

    @Test
    void listBookings_forbiddenForOrganizer() throws Exception {
        User organizer = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/bookings")
                        .with(auth(organiserToken(organizer))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookingService);
    }

 }