package pointsystem.dto.authentication;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username; // Maps to 'name' in the database
    private String password;
    private String email;
    private Boolean isAdmin; // Maps to 'is_admin' in the database
}