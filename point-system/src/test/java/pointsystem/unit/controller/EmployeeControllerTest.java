package pointsystem.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.controller.EmployeeController;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.service.EmployeeService;
import pointsystem.service.SupabaseStorageService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private EmployeeController employeeController;

    public EmployeeControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class);
        supabaseStorageService = mock(SupabaseStorageService.class);
        employeeController = new EmployeeController(employeeService, supabaseStorageService);
    }

    @Test
    void createEmployee_ReturnsCreatedStatus() {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeService.createEmployee(employeeDto)).thenReturn(1);

        // Act
        ResponseEntity<?> response = employeeController.createEmployee(employeeDto);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("id"));
    }

    @Test
    void createEmployee_ThrowsIllegalArgumentException() {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        when(employeeService.createEmployee(employeeDto)).thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        ResponseEntity<?> response = employeeController.createEmployee(employeeDto);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).get("message").toString().contains("Invalid data"));
    }

    @Test
    void testUploadEmployeePhoto_ThrowsException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(supabaseStorageService.uploadEmployeePhoto(file)).thenThrow(new RuntimeException("Upload error"));

        ResponseEntity<?> response = employeeController.uploadEmployeePhoto(file);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Erro no upload de foto"));
    }

    @Test
    void testUpdateEmployeePhoto_ThrowsException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(supabaseStorageService.updateEmployeePhoto(anyString(), eq(file)))
                .thenThrow(new RuntimeException("Update error"));

        ResponseEntity<?> response = employeeController.updateEmployeePhoto("url", file);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Erro em atualizar a foto"));
    }

    @Test
    void testUpdateEmployeeById_ReturnsUpdatedStatus() {
        // Arrange
        int employeeId = 1;
        EmployeeDto employeeDto = new EmployeeDto();

        doNothing().when(employeeService).updateEmployeeById(employeeId, employeeDto);

        // Act
        ResponseEntity<?> response = employeeController.updateEmployeeById(employeeId, employeeDto);

        // Assert
        assertEquals(204, response.getStatusCodeValue()); // 204 No Content esperado
        verify(employeeService, times(1)).updateEmployeeById(employeeId, employeeDto);
        }

    @Test
    void testUpdateEmployeeById_ThrowsIllegalArgumentException(){
        int employeeId = 1;

        EmployeeDto employeeDto = new EmployeeDto();

        doThrow(new IllegalArgumentException("Invalid update data"))
                .when(employeeService)
                .updateEmployeeById(employeeId, employeeDto);

        // Act
        ResponseEntity<?> response = employeeController.updateEmployeeById(employeeId, employeeDto);

        // Assert
        assertEquals(400, response.getStatusCodeValue()); // Bad Request esperado
        assertTrue(((java.util.Map<?, ?>) response.getBody())
                .get("message").toString().contains("Invalid update data"));

    }

    @Test
    void testGetEmployeeById_ReturnsEmployee() {
        int employeeId = 1;
        EmployeeDto dto = new EmployeeDto();
        when(employeeService.getEmployeeById(employeeId)).thenReturn(Optional.of(dto));

        ResponseEntity<EmployeeDto> response = employeeController.getEmployeeById(employeeId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        int employeeId = 1;
        when(employeeService.getEmployeeById(employeeId)).thenReturn(Optional.empty());

        ResponseEntity<EmployeeDto> response = employeeController.getEmployeeById(employeeId);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testGetEmployeeById_ThrowsException() {
        int employeeId = 1;
        when(employeeService.getEmployeeById(employeeId)).thenThrow(new RuntimeException("DB error"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> employeeController.getEmployeeById(employeeId));

        assertEquals(500, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("DB error"));
    }

    @Test
    void testSelectAllEmployee_ReturnsGetStatus() {
        // Arrange
        EmployeeDto employee1 = new EmployeeDto();
        EmployeeDto employee2 = new EmployeeDto();
        List<EmployeeDto> employeeList = List.of(employee1, employee2);

        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        // Act
        ResponseEntity<List<EmployeeDto>> response = employeeController.getAllEmployees();

        // Assert
        assertEquals(200, response.getStatusCodeValue()); // 200 OK esperado
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testSelectAllEmployee_ThrowsResponseStatusException() {
        // Arrange
        when(employeeService.getAllEmployees())
                .thenThrow(new RuntimeException("Invalid update data"));

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            employeeController.getAllEmployees();
        });

        // Assert
        assertEquals(500, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Invalid update data"));
    }

    @Test
    void testTerminateEmployee_ReturnsNoContent() {
        // Arrange
        int employeeId = 1;

        doNothing().when(employeeService).terminateEmployee(employeeId);

        // Act
        ResponseEntity<Void> response = employeeController.terminateEmployee(employeeId);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(employeeService, times(1)).terminateEmployee(employeeId);
    }

    @Test
    void testTerminateEmployee_ThrowsResponseStatusException() {
        // Arrange
        int employeeId = 1;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(employeeService).terminateEmployee(employeeId);

        // Act
        ResponseEntity<Void> response = employeeController.terminateEmployee(employeeId);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testTerminateEmployee_ThrowsGenericException() {
        // Arrange
        int employeeId = 1;

        doThrow(new RuntimeException("Unexpected error"))
                .when(employeeService).terminateEmployee(employeeId);

        // Act
        ResponseEntity<Void> response = employeeController.terminateEmployee(employeeId);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testDeleteEmployeeById_ReturnsNoContent() {
        // Arrange
        int employeeId = 1;

        doNothing().when(employeeService).deleteEmployeeById(employeeId);

        // Act
        ResponseEntity<Void> response = employeeController.deleteEmployeeById(employeeId);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }

    @Test
    void testDeleteEmployeeById_ThrowsException() {
        // Arrange
        int employeeId = 1;

        doThrow(new RuntimeException("Delete failed"))
                .when(employeeService).deleteEmployeeById(employeeId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employeeController.deleteEmployeeById(employeeId);
        });

        assertTrue(exception.getMessage().contains("Delete failed"));
    }

}
