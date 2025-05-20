package pointsystem.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponseDto {
    private String accessToken;
    private Boolean isAdmin;
    private String username;
}