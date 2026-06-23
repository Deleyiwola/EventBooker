package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.config.CustomUserDetailService;
import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.EventDTO;
import dan.springframework.eventbooker.security.JwtAuthenticationFilter;
import dan.springframework.eventbooker.service.EventService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {


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
    private EventService eventService;

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
    void listEvents() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(eventService.listEvents())
                .thenReturn(List.of(new EventDTO(), new EventDTO(), new EventDTO()));

        mockMvc.perform(get("/bookingApi/v1/events")
                .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        verify(eventService).listEvents();

    }

    @Test
    void listEvents_returnsEmptyList() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(eventService.listEvents())
                .thenReturn(List.of());

        mockMvc.perform(get("/bookingApi/v1/events")
                .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(eventService).listEvents();
    }

    @Test
    void getEventById() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(1L);

        when(eventService.getEventById(eventDTO.getId()))
                .thenReturn(eventDTO);

        mockMvc.perform(get("/bookingApi/v1/events/1")
                .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(eventService).getEventById(1L);
    }

    @Test
    void getEventById_notFound() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        when(eventService.getEventById(404L))
                .thenThrow(new NotFoundException("Event with id " + 404L + " not found"));

        mockMvc.perform(get("/bookingApi/v1/events/404")
                .with(auth(userToken(user))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Event with id " + 404L + " not found"));

        verify(eventService).getEventById(404L);
    }

    @Test
    void createEvent() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        EventDTO savedEvent = EventDTO.builder()
                .id(1L)
                .eventName("test")
                .capacity(100)
                .availableSeats(100)
                .bookedSeats(0)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(4))
                .build();

        when(eventService.createEvent(any(EventDTO.class)))
                .thenReturn(savedEvent);

        mockMvc.perform(post("/bookingApi/v1/events")
                .with(auth(organiserToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(savedEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventName").value("test"))
                .andExpect(jsonPath("$.capacity").value(100))
                .andExpect(jsonPath("$.availableSeats").value(100))
                .andExpect(jsonPath("$.bookedSeats").value(0));

        verify(eventService).createEvent(any(EventDTO.class));

    }

    @Test
    void updateEvent() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        EventDTO request = EventDTO.builder()
                .eventName("test")
                .capacity(100)
                .build();

        EventDTO updatedEvent = EventDTO.builder()
                .id(1L)
                .eventName("test")
                .capacity(100)
                .build();

        when(eventService.updateEvent(eq(1L),any()))
                .thenReturn(Optional.of(updatedEvent));

        mockMvc.perform(put("/bookingApi/v1/events/1")
                .with(auth(organiserToken(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventName").value("test"))
                .andExpect(jsonPath("$.capacity").value(100));

        verify(eventService).updateEvent(eq(1L),any());
    }

    @Test
    void updateEvent_notFound() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        EventDTO request = EventDTO.builder()
                .eventName("test")
                .capacity(100)
                .build();

        when(eventService.updateEvent(eq(404L),any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/bookingApi/v1/events/404")
                .with(auth(organiserToken(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Event not found"));

        verify(eventService).updateEvent(eq(404L),any());


    }

    @Test
    void patchEvent() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        EventDTO request = EventDTO.builder()
                .capacity(300)
                .build();

        EventDTO patchedEvent = EventDTO.builder()
                .id(1L)
                .eventName("test")
                .capacity(300)
                .build();

        when(eventService.patchEvent(eq(1L),any()))
                .thenReturn(Optional.of(patchedEvent));

        mockMvc.perform(patch("/bookingApi/v1/events/1")
                .with(auth(organiserToken(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventName").value("test"))
                .andExpect(jsonPath("$.capacity").value(300));

        verify(eventService).patchEvent(eq(1L),any());

    }

    @Test
    void patchEvent_notFound() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        EventDTO request = EventDTO.builder()
                .capacity(300)
                .build();

        when(eventService.patchEvent(eq(404L),any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/bookingApi/v1/events/404")
                .with(auth(organiserToken(user)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Event not found"));

        verify(eventService).patchEvent(eq(404L),any());
    }

    @Test
    void deleteEvent() throws Exception {
        User user = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        when(eventService.deleteEvent(eq(1L)))
                .thenReturn(true);

        mockMvc.perform(delete("/bookingApi/v1/events/1")
                .with(auth(adminToken(user))))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(eq(1L));
    }

    @Test
    void deleteEvent_notFound() throws Exception {
        User user = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        when(eventService.deleteEvent(eq(404L)))
                .thenThrow(new NotFoundException("Event not found"));

        mockMvc.perform(delete("/bookingApi/v1/events/404")
                .with(auth(adminToken(user))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("Event not found"));

        verify(eventService).deleteEvent(eq(404L));

    }

}