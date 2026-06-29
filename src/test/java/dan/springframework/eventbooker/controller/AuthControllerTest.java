package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.config.CustomUserDetailService;
import dan.springframework.eventbooker.exception.ExistingUserException;
import dan.springframework.eventbooker.exception.InvalidCredentialsException;
import dan.springframework.eventbooker.exception.PasswordMismatchException;
import dan.springframework.eventbooker.model.LoginRequest;
import dan.springframework.eventbooker.model.RegisterUser;
import dan.springframework.eventbooker.security.JwtAuthenticationFilter;
import dan.springframework.eventbooker.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonMapper jsonMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private RegisterUser validRegisterUser(){
        RegisterUser r = new RegisterUser();
        r.setFirstName("John");
        r.setLastName("Doe");
        r.setEmail("john@test.com");
        r.setPhoneNumber("1234567890");
        r.setPassword("password");
        r.setConfirmPassword("password");
        return r;
    }

    private LoginRequest validLoginRequest(){
        LoginRequest r = new LoginRequest();
        r.setEmail("john@test.com");
        r.setPassword("password");
        return r;
    }

    @Test
    void register() throws Exception {
        when(authService.register(any(RegisterUser.class)))
                .thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/bookingApi/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validRegisterUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(authService).register(any(RegisterUser.class));
    }

    @Test
    void register_passwordMismatch() throws Exception {
        when(authService.register(any(RegisterUser.class)))
                .thenThrow(new PasswordMismatchException("The passwords do not match"));

        mockMvc.perform(post("/bookingApi/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validRegisterUser())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Password Mismatch"))
                .andExpect(jsonPath("$.message").value("The passwords do not match"));

        verify(authService).register(any(RegisterUser.class));
    }

    @Test
    void register_existingUser() throws Exception {
        when(authService.register(any(RegisterUser.class)))
                .thenThrow(new ExistingUserException("User with this email already exists"));

        mockMvc.perform(post("/bookingApi/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validRegisterUser())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Existing User ERROR"))
                .andExpect(jsonPath("$.message").value("User with this email already exists"));

        verify(authService).register(any(RegisterUser.class));
    }

    @Test
    void register_missingFields() throws Exception {
        RegisterUser invalid = new RegisterUser();

        mockMvc.perform(post("/bookingApi/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void register_PasswordTooShort() throws Exception {
        RegisterUser invalid = new RegisterUser();
        invalid.setPassword("short");
        invalid.setConfirmPassword("short");

        mockMvc.perform(post("/bookingApi/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login() throws Exception {
        when(authService.login(eq("john@test.com"),eq("password")))
                .thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/bookingApi/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validLoginRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(authService).login(eq("john@test.com"),eq("password"));
    }

    @Test
    void login_invalidEmail() throws Exception {
        when(authService.login(any(),any()))
                .thenThrow(new InvalidCredentialsException("Invalid Email"));

        mockMvc.perform(post("/bookingApi/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validLoginRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Invalid Credentials"))
                .andExpect(jsonPath("$.message").value("Invalid Email"));

        verify(authService).login(any(),any());
    }

    @Test
    void login_invalidPassword() throws Exception {
        when(authService.login(any(),any()))
                .thenThrow(new InvalidCredentialsException("Invalid Password"));

        mockMvc.perform(post("/bookingApi/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(validLoginRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Invalid Credentials"))
                .andExpect(jsonPath("$.message").value("Invalid Password"));

        verify(authService).login(any(),any());
    }

    @Test
    void login_missingFields() throws Exception {
        LoginRequest invalid = new LoginRequest();

        mockMvc.perform(post("/bookingApi/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
}