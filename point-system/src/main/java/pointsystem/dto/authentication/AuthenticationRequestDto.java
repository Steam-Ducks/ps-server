package pointsystem.dto.authentication;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String email;
    private String password;
}