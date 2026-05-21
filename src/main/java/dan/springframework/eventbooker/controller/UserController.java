package dan.springframework.eventbooker.controller;

import dan.springframework.eventbooker.exception.NotFoundException;
import dan.springframework.eventbooker.model.UserDTO;
import dan.springframework.eventbooker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    public static final String USER_URI = "/bookingApi/v1/users";
    public static final String USER_ID_URI = "/bookingApi/v1/users/{userId}";

    private final UserService userService;

//    Earmarked for admin role
    @GetMapping(USER_URI)
    public List<UserDTO> listUsers()     {
        return userService.listUsers();
    }

//    Ear marked for admin role but i'll make a proflie endpoint
    @GetMapping(USER_ID_URI)
    public UserDTO getUserById(@PathVariable Long userId) {

        log.info("Getting user with userId: {}", userId);

        return userService.getUserById(userId);
    }
/*
    Not sure if i should remove this,
     because the application can already create a new user from the AuthController */
    @PostMapping(USER_URI)
    public ResponseEntity<UserDTO> createNewUser(@Validated @RequestBody UserDTO userDTO) {

        UserDTO savedUser = userService.createUser(userDTO);

        return ResponseEntity
                .created(URI.create(USER_URI + "/" + savedUser.getId()))
                .body(savedUser);
    }

//Normal user but add security
    @PutMapping(USER_ID_URI)
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @Validated @RequestBody UserDTO userDTO) {
        return userService.updateUser(userId, userDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

//Normal user but add security
    @PatchMapping(USER_ID_URI)
    public ResponseEntity<UserDTO> patchUser(@PathVariable Long userId, @Validated @RequestBody UserDTO userDTO) {
        return userService.patchUser(userId, userDTO)
                .map(ResponseEntity::ok)

                .orElseThrow(() -> new NotFoundException("User not found"));
    }

// Ear marked for admin role
    @DeleteMapping(USER_ID_URI)
    public  ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
