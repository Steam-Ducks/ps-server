package pointsystem.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id; // Optional for creation
    private String username;
    private String email;
    private String password; // Optional for updates, required for creation
    private Boolean isAdmin = false;
    private Boolean isInactive = false;
}