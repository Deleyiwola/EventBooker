package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.UserDTO;
import dan.springframework.eventbooker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    public static final String USER_URI = "/bookingApi/v1/users";
    public static final String USER_ID_URI = "/bookingApi/v1/users/{userId}";
    public static final String MY_PROFILE_URI = "/my-profile";

    private final UserService userService;

    //    Earmarked for admin role
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(USER_URI)
    public List<UserDTO> listUsers()     {
        return userService.listUsers();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(USER_URI + MY_PROFILE_URI)
    public UserDTO getMyProfile(Authentication authentication) {
        log.debug("===>>> {}", ((User)authentication.getPrincipal()).getEmail());
        return userService.getProfile(((User)authentication.getPrincipal()).getEmail());
    }

    //    Ear marked for admin role but i'll make a proflie endpoint
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(USER_ID_URI)
    public UserDTO getUserById(@PathVariable Long userId) {

        log.info("Getting user with userId: {}", userId);

        return userService.getUserById(userId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping(USER_URI+MY_PROFILE_URI)
    public ResponseEntity<UserDTO> updateUser( @Validated @RequestBody UserDTO userDTO,
                                               Authentication authentication) {
        return userService.updateUser(userDTO, ((User)authentication.getPrincipal()).getEmail())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping(USER_URI+MY_PROFILE_URI)
    public ResponseEntity<UserDTO> patchUser(@Validated @RequestBody UserDTO userDTO,
                                             Authentication authentication) {
        return ResponseEntity.ok( userService.patchUser(userDTO, ((User)authentication.getPrincipal()).getEmail()));
    }

    // Ear marked for admin role
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(USER_ID_URI)
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);

        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('USER')")
    @DeleteMapping(USER_URI+MY_PROFILE_URI)
    public  ResponseEntity<Void> deleteUser(Authentication authentication) {
        userService.deleteUser(((User)authentication.getPrincipal()).getEmail());

        return ResponseEntity.noContent().build();
    }
}
