package dan.springframework.eventbooker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
