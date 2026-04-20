package dan.springframework.eventbooker.mapper;

import dan.springframework.eventbooker.entity.User;
import dan.springframework.eventbooker.model.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // User -> DTO
     UserDTO userToUserDTO(User user);

     // DTO -> User
    @Mapping(target = "bookings", ignore = true)
    User userDTOToUser(UserDTO dto);
}
