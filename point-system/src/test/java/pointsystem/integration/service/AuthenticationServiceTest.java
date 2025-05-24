package pointsystem.integration.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import pointsystem.config.JwtUtil;
import pointsystem.dto.authentication.AuthenticationRequestDto;
import pointsystem.dto.authentication.AuthenticationResponseDto;
import pointsystem.dto.authentication.RegisterRequestDto;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;
import pointsystem.service.AuthenticationService;

@SpringBootTest
@Transactional
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private RegisterRequestDto validRegisterRequest;
    private AuthenticationRequestDto validAuthRequest;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        validRegisterRequest = new RegisterRequestDto();
        validRegisterRequest.setEmail("usuario@altave.com"); // Alterado para domínio correto
        validRegisterRequest.setPassword("senhaSegura123");
        validRegisterRequest.setUsername("Usuario Teste");
        validRegisterRequest.setIsAdmin(false);

        validAuthRequest = new AuthenticationRequestDto();
        validAuthRequest.setEmail("usuario@altave.com"); // Alterado para domínio correto
        validAuthRequest.setPassword("senhaSegura123");
    }

    @Test
    public void testRegister_Success() {
        AuthenticationResponseDto response = authenticationService.register(validRegisterRequest);

        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getAccessToken()).isNotBlank();
                    assertThat(r.getIsAdmin()).isFalse();
                    assertThat(r.getUsername()).isEqualTo("Usuario Teste");
                });

        UserEntity savedUser = userRepository.findByEmail("usuario@altave.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(passwordEncoder.matches("senhaSegura123", savedUser.getPassword())).isTrue();
    }

    @Test
    public void testAuthenticate_Success() throws BadRequestException {
        authenticationService.register(validRegisterRequest);

        AuthenticationResponseDto response = authenticationService.authenticate(validAuthRequest);

        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getAccessToken()).isNotBlank();
                    assertThat(r.getIsAdmin()).isFalse();
                    assertThat(r.getUsername()).isEqualTo("Usuario Teste");
                });

        assertThat(jwtUtil.isTokenValid(response.getAccessToken(), "usuario@altave.com")).isTrue();
    }

    @Test
    public void testRegister_AdminUser_SetsAdminFlag() {
        RegisterRequestDto adminRequest = new RegisterRequestDto();
        adminRequest.setEmail("admin@altave.com"); // Domínio correto
        adminRequest.setPassword("admin123");
        adminRequest.setUsername("Admin");
        adminRequest.setIsAdmin(true);

        AuthenticationResponseDto response = authenticationService.register(adminRequest);

        assertThat(response.getIsAdmin()).isTrue();
    }

    @Test
    public void testAuthenticate_AdminUser_ReturnsAdminFlag() throws BadRequestException {
        RegisterRequestDto adminRequest = new RegisterRequestDto();
        adminRequest.setEmail("admin@altave.com"); // Domínio correto
        adminRequest.setPassword("admin123");
        adminRequest.setUsername("Admin");
        adminRequest.setIsAdmin(true);

        authenticationService.register(adminRequest);

        AuthenticationRequestDto adminAuthRequest = new AuthenticationRequestDto();
        adminAuthRequest.setEmail("admin@altave.com"); // Domínio correto
        adminAuthRequest.setPassword("admin123");

        AuthenticationResponseDto response = authenticationService.authenticate(adminAuthRequest);

        assertThat(response.getIsAdmin()).isTrue();
    }

    @Test
    public void testRegister_InvalidDomain_ShouldThrow() {
        RegisterRequestDto invalidRequest = new RegisterRequestDto();
        invalidRequest.setEmail("usuario@dominioerrado.com");
        invalidRequest.setPassword("senha123");
        invalidRequest.setUsername("Usuario Inválido");
        invalidRequest.setIsAdmin(false);

        assertThatThrownBy(() -> authenticationService.register(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O e-mail deve ser do domínio '@altave'");
    }
}