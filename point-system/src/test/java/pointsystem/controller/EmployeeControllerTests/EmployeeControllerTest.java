package pointsystem.controller.EmployeeControllerTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pointsystem.controller.EmployeeController;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.service.EmployeeService;
import pointsystem.service.SupabaseStorageService;
import org.springframework.http.ResponseEntity;

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

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class);
        supabaseStorageService = mock(SupabaseStorageService.class);
        employeeController = new EmployeeController(employeeService, supabaseStorageService);
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
    void testSelectAllEmployee_ReturnsGetStatus(){

        EmployeeDto employeeDto = new EmployeeDto();


    }
}