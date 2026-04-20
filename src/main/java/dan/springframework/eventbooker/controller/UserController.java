package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.UserDTO;
import dan.springframework.eventbooker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    public static final String USER_URI = "/bookingApi/v1/users";
    public static final String USER_ID_URI = "/bookingApi/v1/users/{userId}";

    private final UserService userService;

    @GetMapping(USER_URI)
    public List<UserDTO> listUsers()     {
        return userService.listUsers();
    }

    @GetMapping(USER_ID_URI)
    public UserDTO getUserById(@PathVariable UUID userId) {

        log.info("Getting user with userId: {}", userId);

        return userService.getUserById(userId);
    }

    @PostMapping(USER_URI)
    public ResponseEntity<UserDTO> createNewUser(@RequestBody UserDTO userDTO) {

        UserDTO savedUser = userService.createUser(userDTO);

        return ResponseEntity
                .created(URI.create(USER_URI + "/" + savedUser.getId()))
                .body(savedUser);
    }

    @PutMapping(USER_ID_URI)
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID userId, @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId, userDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @PatchMapping(USER_ID_URI)
    public ResponseEntity<UserDTO> patchUser(@PathVariable UUID userId, @RequestBody UserDTO userDTO) {
        return userService.patchUser(userId, userDTO)
                .map(ResponseEntity::ok)

                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @DeleteMapping(USER_ID_URI)
    public  ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
