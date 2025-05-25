package pointsystem.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pointsystem.controller.UserController;
import pointsystem.dto.user.UserDto;
import pointsystem.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserSuccess() {
        UserDto dto = new UserDto();
        when(userService.createUser(dto)).thenReturn(10);

        ResponseEntity<?> response = userController.createUser(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Map.of("id", 10), response.getBody());
        verify(userService).createUser(dto);
    }

    @Test
    void testCreateUserBadRequest() {
        UserDto dto = new UserDto();
        when(userService.createUser(dto)).thenThrow(new IllegalArgumentException("Dados inválidos"));

        ResponseEntity<?> response = userController.createUser(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("message", "Dados inválidos"), response.getBody());
    }

    @Test
    void testCreateUserInternalServerError() {
        UserDto dto = new UserDto();
        when(userService.createUser(dto)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = userController.createUser(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Map.of("message", "Erro ao cadastrar o Usuario. tente novamente"), response.getBody());
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> users = Arrays.asList(new UserDto(), new UserDto());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void testGetUserByIdFound() {
        UserDto user = new UserDto();
        when(userService.getUserById(1)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.getUserById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUserById(1)).thenReturn(null);

        ResponseEntity<UserDto> response = userController.getUserById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateUser() {
        UserDto dto = new UserDto();

        ResponseEntity<Void> response = userController.updateUser(1, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).updateUserById(1, dto);
    }
}
