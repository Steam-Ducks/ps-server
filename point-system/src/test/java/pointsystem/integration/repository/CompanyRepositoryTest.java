package pointsystem.integration.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void shouldReturnTrueWhenCnpjExists() {
        Company company = new Company();
        company.setName("Sabor do Reino");
        company.setCnpj("12345678000199");
        companyRepository.save(company);

        boolean exists = companyRepository.existsByCnpj("12345678000199");
        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseWhenCnpjDoesNotExist() {
        boolean exists = companyRepository.existsByCnpj("00000000000000");
        assertFalse(exists);
    }

    @Test
    public void shouldReturnAllCompanyIds() {
        Company company1 = new Company();
        company1.setName("Empresa A");
        company1.setCnpj("11111111000111");

        Company company2 = new Company();
        company2.setName("Empresa B");
        company2.setCnpj("22222222000122");

        companyRepository.saveAll(List.of(company1, company2));

        List<Integer> ids = companyRepository.findAllCompanyIds();
        assertEquals(2, ids.size());
        assertTrue(ids.contains(company1.getId()));
        assertTrue(ids.contains(company2.getId()));
    }
}

