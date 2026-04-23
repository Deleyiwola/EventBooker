package dan.springframework.eventbooker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;


//    @NotNull
//    @NotBlank
    private String name;

//    @NotNull
//    @NotBlank
    private String phoneNumber;

//    @NotNull
//    @NotBlank
    private String email;
}
