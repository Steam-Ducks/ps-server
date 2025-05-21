package pointsystem.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.entity.Employee;
import pointsystem.repository.EmployeeRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void shouldReturnTrueWhenCpfExists() {
        Employee employee = new Employee();
        employee.setName("Carlos Daniel");
        employee.setCpf("12345678900");
        employee.setStatus(true);
        employeeRepository.save(employee);

        boolean exists = employeeRepository.existsByCpf("12345678900");
        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseWhenCpfDoesNotExist() {
        boolean exists = employeeRepository.existsByCpf("00000000000");
        assertFalse(exists);
    }
}

