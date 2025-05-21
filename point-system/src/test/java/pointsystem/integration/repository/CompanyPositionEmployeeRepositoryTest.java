package pointsystem.integration.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pointsystem.entity.Company;
import pointsystem.entity.CompanyPositionEmployee;
import pointsystem.entity.Employee;
import pointsystem.repository.CompanyPositionEmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CompanyPositionEmployeeRepositoryTest {

    @Autowired
    private CompanyPositionEmployeeRepository companyPositionEmployeeRepository;

    @Test
    @DisplayName("Deve buscar CompanyPositionEmployee por id do Employee")
    public void testFindByEmployeeId() {
        // Criar employee
        Employee emp = new Employee();
        emp.setName("Funcionario Teste");
        emp.setCpf("99999999999");
        emp.setStatus(true);
        // Aqui você precisa salvar emp no repositório EmployeeRepository,
        // mas como não temos esse repo aqui, imagino que você já tenha.
        // Para este exemplo, vamos supor que você tenha um repositório employeeRepository autowired e salve:
        // employeeRepository.save(emp);
        // Se não tiver, faça um mock ou configure.

        // Criar company
        Company company = new Company();
        company.setName("Empresa Teste");
        // similarmente, salve company

        // Criar CompanyPositionEmployee
        CompanyPositionEmployee cpe = new CompanyPositionEmployee();
        cpe.setEmployee(emp);
        cpe.setCompany(company);

        // salvar cpe
        companyPositionEmployeeRepository.save(cpe);

        Optional<CompanyPositionEmployee> result = companyPositionEmployeeRepository.findByEmployeeId(emp.getId());

        assertTrue(result.isPresent());
        assertEquals(emp.getId(), result.get().getEmployee().getId());
    }

    @Test
    @DisplayName("Deve buscar lista de ids de employees por id da Company")
    public void testFindByCompanyId() {
        // Setup igual ao anterior
        Employee emp1 = new Employee();
        emp1.setName("Emp1");
        emp1.setCpf("11111111111");
        emp1.setStatus(true);
        // employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setName("Emp2");
        emp2.setCpf("22222222222");
        emp2.setStatus(true);
        // employeeRepository.save(emp2);

        Company company = new Company();
        company.setName("Empresa XYZ");
        // companyRepository.save(company);

        CompanyPositionEmployee cpe1 = new CompanyPositionEmployee();
        cpe1.setEmployee(emp1);
        cpe1.setCompany(company);

        CompanyPositionEmployee cpe2 = new CompanyPositionEmployee();
        cpe2.setEmployee(emp2);
        cpe2.setCompany(company);

        companyPositionEmployeeRepository.save(cpe1);
        companyPositionEmployeeRepository.save(cpe2);

        List<Integer> employeeIds = companyPositionEmployeeRepository.findByCompanyId(company.getId());

        assertNotNull(employeeIds);
        assertEquals(2, employeeIds.size());
        assertTrue(employeeIds.contains(emp1.getId()));
        assertTrue(employeeIds.contains(emp2.getId()));
    }
}

