package dan.springframework.eventbooker.mapper;

import dan.springframework.eventbooker.entity.OrganizerRequest;
import dan.springframework.eventbooker.model.OrganizerRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizerRequestMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    OrganizerRequestDTO organizerRequestToOrganizerRequestDTO(OrganizerRequest organizerRequest);

}
