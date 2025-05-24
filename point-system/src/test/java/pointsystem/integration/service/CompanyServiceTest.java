package pointsystem.integration.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pointsystem.converter.CompanyConverter;
import pointsystem.dto.company.CompanyDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;
import pointsystem.service.CompanyService;
import pointsystem.service.EmployeeService;
import pointsystem.service.TimeRecordsService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyConverter companyConverter;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private TimeRecordsService timeRecordsService;

    private CompanyDto sampleCompanyDto;

    @BeforeEach
    public void setup() {
        sampleCompanyDto = new CompanyDto();
        sampleCompanyDto.setCnpj("12345678000199");
        sampleCompanyDto.setName("Empresa Teste");
        sampleCompanyDto.setContact("contato@empresa.com"); // Campo obrigatório adicionado
    }

    @Test
    public void testCreateAndGetCompany() {
        int companyId = companyService.createCompany(sampleCompanyDto);

        Optional<CompanyDto> companyOpt = companyService.getCompanyById(companyId);

        assertThat(companyOpt).isPresent();

        CompanyDto company = companyOpt.get();
        assertThat(company.getCnpj()).isEqualTo(sampleCompanyDto.getCnpj());
        assertThat(company.getName()).isEqualTo(sampleCompanyDto.getName());
        assertThat(company.getContact()).isEqualTo(sampleCompanyDto.getContact()); // Verificação adicional
    }

    @Test
    public void testCreateCompanyWithDuplicateCnpj_shouldThrow() {
        companyService.createCompany(sampleCompanyDto);

        CompanyDto duplicateDto = new CompanyDto();
        duplicateDto.setCnpj(sampleCompanyDto.getCnpj());
        duplicateDto.setName("Outra Empresa");
        duplicateDto.setContact("(11) 8888-8888"); // Campo obrigatório se existir

        assertThatThrownBy(() -> companyService.createCompany(duplicateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CNPJ já está em uso");
    }

    @Test
    public void testUpdateCompany() {
        int companyId = companyService.createCompany(sampleCompanyDto);

        CompanyDto updateDto = new CompanyDto();
        updateDto.setCnpj("98765432000100");
        updateDto.setName("Empresa Atualizada");
        updateDto.setContact("novo@contato.com"); // Campo obrigatório

        companyService.updateCompanyById(companyId, updateDto);

        Optional<CompanyDto> updatedCompany = companyService.getCompanyById(companyId);
        assertThat(updatedCompany).isPresent();
        assertThat(updatedCompany.get().getName()).isEqualTo("Empresa Atualizada");
        assertThat(updatedCompany.get().getCnpj()).isEqualTo("98765432000100");
        assertThat(updatedCompany.get().getContact()).isEqualTo("novo@contato.com"); // Verificação adicional
    }

    @Test
    public void testDeleteCompany() {
        int companyId = companyService.createCompany(sampleCompanyDto);
        companyService.deleteCompanyById(companyId);

        Optional<CompanyDto> deletedCompany = companyService.getCompanyById(companyId);
        assertThat(deletedCompany).isEmpty();
    }

    @Test
    public void testGetAllCompanies() {
        companyService.createCompany(sampleCompanyDto);

        List<CompanyDto> companies = companyService.getAllCompanies();
        assertThat(companies).isNotEmpty();
        assertThat(companies.stream().map(CompanyDto::getName))
                .contains(sampleCompanyDto.getName());
    }
}