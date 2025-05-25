package pointsystem.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.entity.Company;
import pointsystem.entity.Position;
import pointsystem.repository.CompanyRepository;
import pointsystem.repository.EmployeeRepository;
import pointsystem.repository.PositionRepository;
import pointsystem.service.EmployeeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    private Integer companyId;
    private Integer positionId;

    @BeforeEach
    void setup() {
        Company company = new Company();
        company.setName("Test Company");
        company.setCnpj("12345678000190"); // valor fictício válido
        company.setContact("contact@test.com"); // se também for not-null
        companyId = companyRepository.save(company).getId(); // <-- AQUI

        Position position = new Position();
        position.setName("Developer");
        positionId = positionRepository.save(position).getId();
    }


    @Test
    void testCreateAndGetEmployee() {
        EmployeeDto dto = new EmployeeDto();
        dto.setName("Carlos");
        dto.setCpf("123.456.789-00");
        dto.setCompanyId(companyId);
        dto.setPositionId(positionId);
        dto.setSalary(50.0F);
        dto.setStartDate(LocalDate.now());

        int employeeId = employeeService.createEmployee(dto);

        Optional<EmployeeDto> result = employeeService.getEmployeeById(employeeId);
        assertTrue(result.isPresent());
        assertEquals("Carlos", result.get().getName());
    }

    @Test
    void testUpdateEmployee() {
        EmployeeDto dto = new EmployeeDto();
        dto.setName("João");
        dto.setCpf("987.654.321-00");
        dto.setCompanyId(companyId);
        dto.setPositionId(positionId);
        dto.setSalary(60.0F);
        dto.setStartDate(LocalDate.now());

        int id = employeeService.createEmployee(dto);

        EmployeeDto updated = new EmployeeDto();
        updated.setName("João Silva");
        updated.setSalary(70.0F);

        employeeService.updateEmployeeById(id, updated);

        Optional<EmployeeDto> updatedEmployee = employeeService.getEmployeeById(id);
        assertTrue(updatedEmployee.isPresent());
        assertEquals("João Silva", updatedEmployee.get().getName());
    }

    @Test
    void testCountEmployeesByMonth() {
        EmployeeDto dto = new EmployeeDto();
        dto.setName("Ana");
        dto.setCpf("333.333.333-33");
        dto.setCompanyId(companyId);
        dto.setPositionId(positionId);
        dto.setSalary(50F);
        dto.setStartDate(LocalDate.of(2025, 5, 1));

        int id = employeeService.createEmployee(dto);

        List<Integer> ids = List.of(id);
        Map<String, Integer> count = employeeService.countEmployeesByMonth(
                ids,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 31)
        );

        assertTrue(count.containsKey("2025-05-01"));
        assertEquals(1, count.get("2025-05-01"));
    }

    @Test
    void testTerminateEmployee() {
        EmployeeDto dto = new EmployeeDto();
        dto.setName("Maria");
        dto.setCpf("444.444.444-44");
        dto.setCompanyId(companyId);
        dto.setPositionId(positionId);
        dto.setSalary(30F);
        dto.setStartDate(LocalDate.now());

        int id = employeeService.createEmployee(dto);

        employeeService.terminateEmployee(id);

        Optional<EmployeeDto> result = employeeService.getEmployeeById(id);
        assertTrue(result.isPresent());
        assertFalse(result.get().getStatus());
    }
}
