package pointsystem.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.EmployeeConverter;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.entity.*;
import pointsystem.repository.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private PositionRepository positionRepository;
    @Mock private CompanyPositionEmployeeRepository companyPositionEmployeeRepository;
    @Mock private EmployeeConverter employeeConverter;

    @InjectMocks
    private pointsystem.service.EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEmployee_ReturnsId_WhenSuccessful() {

        Company company = new Company();
        Position position = new Position();
        EmployeeDto dto = EmployeeDto.builder()
                .cpf("123.456.789-00")
                .companyId(1)
                .positionId(1)
                .salary(2000f)
                .build();
        Employee employee = new Employee();
        employee.setId(42);
        when(employeeRepository.existsByCpf(dto.getCpf())).thenReturn(false);
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(positionRepository.findById(1)).thenReturn(Optional.of(position));
        when(employeeConverter.toEntity(dto)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);


        int resultId = employeeService.createEmployee(dto);


        assertEquals(42, resultId);
        verify(companyPositionEmployeeRepository).save(any(CompanyPositionEmployee.class));
    }

    @Test
    void createEmployee_ThrowsIllegalArgument_WhenCpfInvalid() {

        EmployeeDto dto = EmployeeDto.builder().cpf("invalidCPF").build();


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(dto));
        assertEquals("CPF inválido.", exception.getMessage());
    }

    @Test
    void createEmployee_ThrowsIllegalArgument_WhenCpfExists() {

        EmployeeDto dto = EmployeeDto.builder().cpf("123.456.789-00").build();
        when(employeeRepository.existsByCpf(dto.getCpf())).thenReturn(true);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(dto));
        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    @Test
    void getEmployeeById_ReturnsDto_WhenFound() {

        Employee employee = new Employee();
        employee.setId(1);
        EmployeeDto dto = new EmployeeDto();
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeConverter.toDto(employee)).thenReturn(dto);


        Optional<EmployeeDto> result = employeeService.getEmployeeById(1);


        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getAllEmployees_ReturnsSortedActiveDtos() {

        Employee active = new Employee();
        active.setStatus(true);
        active.setName("Carlos");

        Employee inactive = new Employee();
        inactive.setStatus(false);

        List<Employee> list = Arrays.asList(active, inactive);
        List<EmployeeDto> expectedDtos = Collections.singletonList(new EmployeeDto());
        when(employeeRepository.findAll()).thenReturn(list);
        when(employeeConverter.toDto(anyList())).thenReturn(expectedDtos);


        List<EmployeeDto> result = employeeService.getAllEmployees();


        assertEquals(expectedDtos, result);
    }

    @Test
    void terminateEmployee_SetsStatusAndClearsRelations() {

        int employeeId = 1;
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setStatus(true);
        CompanyPositionEmployee pivot = new CompanyPositionEmployee();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(companyPositionEmployeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(pivot));


        employeeService.terminateEmployee(employeeId);


        assertFalse(employee.getStatus());
        assertNotNull(employee.getTerminationDate());
        assertNull(pivot.getCompany());
        assertNull(pivot.getPosition());
        assertEquals(0, pivot.getSalary());
        verify(employeeRepository).save(employee);
        verify(companyPositionEmployeeRepository).save(pivot);
    }

    @Test
    void terminateEmployee_ThrowsException_WhenEmployeeNotFound() {

        int employeeId = 10;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());


        assertThrows(ResponseStatusException.class, () -> employeeService.terminateEmployee(employeeId));
    }

    @Test
    void deleteEmployeeById_Deletes_WhenExists() {

        int employeeId = 5;
        when(employeeRepository.existsById(employeeId)).thenReturn(true);


        employeeService.deleteEmployeeById(employeeId);


        verify(employeeRepository).deleteById(employeeId);
    }

    @Test
    void countEmployeesByMonth_ReturnsMapOfCounts() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        Employee emp1 = new Employee();
        emp1.setStartDate(LocalDate.of(2024, 1, 5));
        Employee emp2 = new Employee();
        emp2.setStartDate(LocalDate.of(2024, 1, 5));
        when(employeeRepository.findAllById(anyList())).thenReturn(Arrays.asList(emp1, emp2));


        Map<String, Integer> result = employeeService.countEmployeesByMonth(List.of(1, 2), start, end);


        assertEquals(1, result.size());
        assertEquals(2, result.get("2024-01-05"));
    }
}