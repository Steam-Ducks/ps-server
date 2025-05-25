package pointsystem.unit.controller;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pointsystem.controller.AuthController;
import pointsystem.dto.authentication.AuthenticationRequestDto;
import pointsystem.dto.authentication.AuthenticationResponseDto;
import pointsystem.dto.authentication.RegisterRequestDto;
import pointsystem.service.AuthenticationService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ReturnsOkStatus_WhenSuccessful() {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        AuthenticationResponseDto responseDto = new AuthenticationResponseDto("token123", true, "user1");

        when(authenticationService.register(requestDto)).thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = authController.register(requestDto);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void register_ReturnsBadRequest_WhenIllegalArgument() {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        when(authenticationService.register(requestDto))
                .thenThrow(new IllegalArgumentException("Dados inválidos"));

        // Act
        ResponseEntity<?> response = authController.register(requestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Dados inválidos"));
    }

    @Test
    void register_ReturnsInternalServerError_WhenUnexpectedException() {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        when(authenticationService.register(requestDto))
                .thenThrow(new RuntimeException("Erro no sistema"));

        // Act
        ResponseEntity<?> response = authController.register(requestDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Erro ao cadastrar o Usuario. Tente novamente."));
    }

    @Test
    void login_ReturnsOkStatus_WhenSuccessful() throws BadRequestException {
        // Arrange
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        AuthenticationResponseDto responseDto = new AuthenticationResponseDto("token456", false, "user2");

        when(authenticationService.authenticate(requestDto)).thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = authController.login(requestDto);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void login_ReturnsBadRequest_WhenBadRequestException() throws Exception {
        // Arrange
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        when(authenticationService.authenticate(requestDto))
                .thenThrow(new BadRequestException("Login ou senha inválidos"));

        // Act
        ResponseEntity<?> response = authController.login(requestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Erro ao realizar o login. Tente novamente."));
    }

    @Test
    void login_ReturnsInternalServerError_WhenUnexpectedException() throws BadRequestException {
        // Arrange
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        when(authenticationService.authenticate(requestDto))
                .thenThrow(new RuntimeException("Erro inesperado"));

        // Act
        ResponseEntity<?> response = authController.login(requestDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Erro ao realizar o login. Tente novamente."));
    }
}
