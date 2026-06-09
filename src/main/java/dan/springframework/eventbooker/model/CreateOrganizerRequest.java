package dan.springframework.eventbooker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrganizerRequest(

        @NotBlank
        String organizationName,

        @NotBlank
        @Size(max = 1000)
        String description
) {
}
