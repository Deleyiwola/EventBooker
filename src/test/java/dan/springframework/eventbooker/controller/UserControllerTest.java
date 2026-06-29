package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.config.CustomUserDetailService;
import dan.springframework.eventbooker.entity.Role;
import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.UserDTO;
import dan.springframework.eventbooker.security.JwtAuthenticationFilter;
import dan.springframework.eventbooker.service.UserService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

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
    private UserService userService;

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
    void listUsers() throws Exception {
        User admin = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        when(userService.listUsers())
                .thenReturn(List.of(new UserDTO(),new  UserDTO(),new  UserDTO()));

        mockMvc.perform(get("/bookingApi/v1/users")
                .with(auth(adminToken(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        verify(userService).listUsers();
    }

    @Test
    void listUsers_forbiddenForOrganizer() throws Exception {
        User organizer = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/users")
                        .with(auth(userToken(organizer))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void listUsers_forbiddenForUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/users")
                        .with(auth(userToken(user))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void getMyProfile() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        UserDTO userProfile = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .phoneNumber("123456789")
                .build();

        when(userService.getProfile("user@test.com"))
                .thenReturn(userProfile);

        mockMvc.perform(get("/bookingApi/v1/users/my-profile")
                .with(auth(userToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("user@test.com"));

        verify(userService).getProfile("user@test.com");
    }

    @Test
    void getMyProfile_accessibleByAdmin() throws Exception {
        User user = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        UserDTO userProfile = UserDTO.builder()
                .id(1L)
                .name("Test Admin")
                .email("admin@test.com")
                .phoneNumber("123456789")
                .build();

        when(userService.getProfile("admin@test.com"))
                .thenReturn(userProfile);

        mockMvc.perform(get("/bookingApi/v1/users/my-profile")
                .with(auth(adminToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Admin"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("admin@test.com"));

        verify(userService).getProfile("admin@test.com");


    }

    @Test
    void getMyProfile_accessibleByOrganizer() throws Exception {
        User user = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        UserDTO userProfile = UserDTO.builder()
                .id(1L)
                .name("Test Organizer")
                .email("organizer@test.com")
                .phoneNumber("123456789")
                .build();

        when(userService.getProfile("organizer@test.com"))
                .thenReturn(userProfile);

        mockMvc.perform(get("/bookingApi/v1/users/my-profile")
                        .with(auth(adminToken(user))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Organizer"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("organizer@test.com"));

        verify(userService).getProfile("organizer@test.com");


    }

    @Test
    void getUserById() throws Exception {
        User admin = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();


        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .phoneNumber("123456789")
                .build();

        when(userService.getUserById(1L))
                .thenReturn(userDTO);

        mockMvc.perform(get("/bookingApi/v1/users/1")
                .with(auth(adminToken(admin))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.email").value("user@test.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_notFound() throws Exception {
        User admin = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        when(userService.getUserById(404L))
                .thenThrow(new NotFoundException("User with id '404' not found"));

        mockMvc.perform(get("/bookingApi/v1/users/404")
                .with(auth(adminToken(admin))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("User with id '404' not found"));
    }

    @Test
    void getUserById_forbiddenForOrganizer() throws Exception {
        User organizer = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/users/1")
                .with(auth(organiserToken(organizer))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_forbiddenForUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(get("/bookingApi/v1/users/1")
                .with(auth(userToken(user))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void updateUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        UserDTO request = UserDTO.builder()
                .name("Updated Name")
                .phoneNumber("0987654321")
                .build();

        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .name("Updated Name")
                .email("user@test.com")
                .phoneNumber("0987654321")
                .build();

        when(userService.updateUser(any(),eq("user@test.com")))
                .thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/bookingApi/v1/users/my-profile")
                        .with(auth(userToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phoneNumber").value("0987654321"));
        verify(userService).updateUser(any(),eq("user@test.com"));
    }

    @Test
    void patchUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        UserDTO request = UserDTO.builder()
                .phoneNumber("7777777")
                .build();

        UserDTO patchedUser = UserDTO.builder()
                .id(1L)
                .name("Patched User")
                .email("user@test.com")
                .phoneNumber("7777777")
                .build();

        when(userService.patchUser(any(),eq("user@test.com")))
                .thenReturn(patchedUser);

        mockMvc.perform(patch("/bookingApi/v1/users/my-profile")
                        .with(auth(userToken(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Patched User"))
                .andExpect(jsonPath("$.phoneNumber").value("7777777"));

        verify(userService).patchUser(any(UserDTO.class),eq("user@test.com"));


    }

    @Test
    void deleteUserById() throws Exception {
        User admin = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        mockMvc.perform(delete("/bookingApi/v1/users/1")
                .with(auth(adminToken(admin))))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserById(1L);
    }

    @Test
    void deleteUserById_notFound() throws Exception {
        User admin = User.builder()
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        doThrow(new NotFoundException("User with id '404' not found"))
                .when(userService).deleteUserById(404L);

        mockMvc.perform(delete("/bookingApi/v1/users/404")
                .with(auth(adminToken(admin))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found ERROR"))
                .andExpect(jsonPath("$.message").value("User with id '404' not found"));

        verify(userService).deleteUserById(404L);
    }

    @Test
    void deleteUserById_forbiddenForUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(delete("/bookingApi/v1/users/1")
                .with(auth(userToken(user))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void deleteUserById_forbiddenForOrganizer() throws Exception {
        User organizer = User.builder()
                .email("organizer@test.com")
                .role(Role.ROLE_ORGANIZER)
                .build();

        mockMvc.perform(delete("/bookingApi/v1/users/1")
                .with(auth(organiserToken(organizer))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void deleteUser() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .role(Role.ROLE_USER)
                .build();

        mockMvc.perform(delete("/bookingApi/v1/users/my-profile")
                .with(auth(userToken(user))))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("user@test.com");
    }
}