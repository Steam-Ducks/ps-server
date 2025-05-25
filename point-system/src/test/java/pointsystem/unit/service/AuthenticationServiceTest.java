package pointsystem.unit.service;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import pointsystem.config.JwtUtil;
import pointsystem.converter.UserConverter;
import pointsystem.dto.authentication.*;
import pointsystem.entity.UserEntity;
import pointsystem.repository.UserRepository;
import pointsystem.service.AuthenticationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserConverter userConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("usuario");
        request.setEmail("usuario@altave.com");
        request.setPassword("senha123");

        UserEntity userEntity = spy(new UserEntity());
        userEntity.setUsername("usuario");
        userEntity.setEmail("usuario@altave.com");
        userEntity.setPassword("senha123");
        userEntity.setIsAdmin(false);

        when(userConverter.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(userEntity.isEmailvalidador()).thenReturn(true);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(jwtUtil.generateToken("usuario@altave.com", false)).thenReturn("token-jwt");

        AuthenticationResponseDto response = authenticationService.register(request);

        assertEquals("token-jwt", response.getAccessToken());
        assertFalse(response.getIsAdmin());
        assertEquals("usuario", response.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalidOnRegistration() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("usuario");
        request.setEmail("usuario@outrodominio.com");
        request.setPassword("senha123");

        UserEntity userEntity = spy(new UserEntity());
        userEntity.setEmail("usuario@outrodominio.com");
        userEntity.setPassword("senha123");

        when(userConverter.toEntity(request)).thenReturn(userEntity);
        when(userEntity.isEmailvalidador()).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(request);
        });

        assertEquals("O e-mail deve ser do domínio '@altave'", exception.getMessage());
    }

    @Test
    void shouldAuthenticateSuccessfully() throws BadRequestException {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("usuario@altave.com");
        request.setPassword("senha123");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("usuario@altave.com");
        userEntity.setIsAdmin(true);
        userEntity.setUsername("usuario");

        when(userRepository.findByEmail("usuario@altave.com")).thenReturn(Optional.of(userEntity));
        when(jwtUtil.generateToken("usuario@altave.com", true)).thenReturn("token-jwt");

        AuthenticationResponseDto response = authenticationService.authenticate(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("token-jwt", response.getAccessToken());
        assertTrue(response.getIsAdmin());
        assertEquals("usuario", response.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenEmailOrPasswordIsInvalidOnAuthentication() {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("usuario@altave.com");
        request.setPassword("senhaIncorreta");

        doThrow(new RuntimeException()).when(authenticationManager).authenticate(any());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals("E-mail e/ou senha incorretos. Tente novamente.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnAuthentication() {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmail("usuario@altave.com");
        request.setPassword("senha123");

        when(userRepository.findByEmail("usuario@altave.com")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals("E-mail e/ou senha incorretos. Tente novamente.", exception.getMessage());
    }
}