package pointsystem.integration.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.entity.Company;
import pointsystem.entity.CompanyPositionEmployee;
import pointsystem.entity.Employee;
import pointsystem.repository.CompanyPositionEmployeeRepository;
import pointsystem.repository.CompanyRepository;
import pointsystem.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CompanyPositionEmployeeRepositoryTest {

    @Autowired
    private CompanyPositionEmployeeRepository companyPositionEmployeeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Deve buscar CompanyPositionEmployee por id do Employee")
    public void testFindByEmployeeId() {
        // Criar e salvar o employee
        Employee emp = new Employee();
        emp.setName("Funcionario Teste");
        emp.setCpf("99999999999");
        emp.setStatus(true);
        emp = employeeRepository.save(emp);

        // Criar e salvar a company
        Company company = new Company();
        company.setName("Empresa Teste");
        company.setCnpj("12345678000199"); // Adicione esse campo se for obrigatório
        company = companyRepository.save(company);

        // Criar e salvar a associação
        CompanyPositionEmployee cpe = new CompanyPositionEmployee();
        cpe.setEmployee(emp);
        cpe.setCompany(company);
        companyPositionEmployeeRepository.save(cpe);

        // Testar
        Optional<CompanyPositionEmployee> result = companyPositionEmployeeRepository.findByEmployeeId(emp.getId());

        assertTrue(result.isPresent());
        assertEquals(emp.getId(), result.get().getEmployee().getId());
    }

    @Test
    @DisplayName("Deve buscar lista de ids de employees por id da Company")
    public void testFindByCompanyId() {
        // Criar e salvar os employees
        Employee emp1 = new Employee();
        emp1.setName("Emp1");
        emp1.setCpf("11111111111");
        emp1.setStatus(true);
        emp1 = employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setName("Emp2");
        emp2.setCpf("22222222222");
        emp2.setStatus(true);
        emp2 = employeeRepository.save(emp2);

        // Criar e salvar a company
        Company company = new Company();
        company.setName("Empresa XYZ");
        company.setCnpj("98765432000100");
        company = companyRepository.save(company);

        // Criar e salvar as associações
        CompanyPositionEmployee cpe1 = new CompanyPositionEmployee();
        cpe1.setEmployee(emp1);
        cpe1.setCompany(company);
        companyPositionEmployeeRepository.save(cpe1);

        CompanyPositionEmployee cpe2 = new CompanyPositionEmployee();
        cpe2.setEmployee(emp2);
        cpe2.setCompany(company);
        companyPositionEmployeeRepository.save(cpe2);

        // Testar
        List<Integer> employeeIds = companyPositionEmployeeRepository.findByCompanyId(company.getId());

        assertNotNull(employeeIds);
        assertEquals(2, employeeIds.size());
        assertTrue(employeeIds.contains(emp1.getId()));
        assertTrue(employeeIds.contains(emp2.getId()));
    }
}
