package pointsystem.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pointsystem.converter.CompanyConverter;
import pointsystem.dto.company.CompanyDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;
import pointsystem.service.CompanyService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyConverter companyConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCompany_ShouldSaveAndReturnId_WhenCnpjIsNotUsed() {
        CompanyDto dto = CompanyDto.builder()
                .name("Steam Ducks")
                .cnpj("12345678000195")
                .contact("steamducks@altave.com")
                .build();

        Company company = new Company();
        company.setId(1);
        company.setName(dto.getName());
        company.setCnpj(dto.getCnpj());
        company.setContact(dto.getContact());

        when(companyRepository.existsByCnpj(dto.getCnpj())).thenReturn(false);
        when(companyConverter.toEntity(dto)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);

        int result = companyService.createCompany(dto);

        assertEquals(1, result);
        verify(companyRepository).save(company);
    }

    @Test
    void createCompany_ShouldThrowException_WhenCnpjExists() {
        CompanyDto dto = CompanyDto.builder()
                .name("Vortek")
                .cnpj("98765432000123")
                .contact("vortek@altave.com")
                .build();

        when(companyRepository.existsByCnpj(dto.getCnpj())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> companyService.createCompany(dto));
    }

    @Test
    void getCompanyById_ShouldReturnCompanyDto_WhenCompanyExists() {
        Company company = new Company();
        company.setId(1);
        company.setName("DenariusData");
        company.setCnpj("11122233000144");
        company.setContact("denariusdata@altave.com");

        CompanyDto dto = CompanyDto.builder()
                .id(1)
                .name("DenariusData")
                .cnpj("11122233000144")
                .contact("denariusdata@altave.com")
                .build();

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(companyConverter.toDto(company)).thenReturn(dto);

        Optional<CompanyDto> result = companyService.getCompanyById(1);

        assertTrue(result.isPresent());
        assertEquals(dto.getName(), result.get().getName());
        assertEquals(dto.getContact(), result.get().getContact());
    }

    @Test
    void getCompanyById_ShouldReturnEmpty_WhenCompanyDoesNotExist() {
        when(companyRepository.findById(99)).thenReturn(Optional.empty());

        Optional<CompanyDto> result = companyService.getCompanyById(99);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllCompanies_ShouldReturnListOfCompanyDtosSortedByName() {
        Company c1 = new Company();
        c1.setId(1);
        c1.setName("Steam Ducks");
        c1.setCnpj("12345678000195");
        c1.setContact("steamducks@altave.com");

        Company c2 = new Company();
        c2.setId(2);
        c2.setName("Vortek");
        c2.setCnpj("98765432000123");
        c2.setContact("vortek@altave.com");

        Company c3 = new Company();
        c3.setId(3);
        c3.setName("DenariusData");
        c3.setCnpj("11122233000144");
        c3.setContact("denariusdata@altave.com");

        List<Company> entities = List.of(c1, c2, c3);

        CompanyDto dto1 = CompanyDto.builder()
                .id(1)
                .name("Steam Ducks")
                .cnpj("12345678000195")
                .contact("steamducks@altave.com")
                .build();

        CompanyDto dto2 = CompanyDto.builder()
                .id(2)
                .name("Vortek")
                .cnpj("98765432000123")
                .contact("vortek@altave.com")
                .build();

        CompanyDto dto3 = CompanyDto.builder()
                .id(3)
                .name("DenariusData")
                .cnpj("11122233000144")
                .contact("denariusdata@altave.com")
                .build();

        when(companyRepository.findAll()).thenReturn(entities);
        when(companyConverter.toDto(c1)).thenReturn(dto1);
        when(companyConverter.toDto(c2)).thenReturn(dto2);
        when(companyConverter.toDto(c3)).thenReturn(dto3);

        List<CompanyDto> result = companyService.getAllCompanies();

        assertEquals(3, result.size());

        assertEquals("DenariusData", result.get(0).getName());
        assertEquals("Steam Ducks", result.get(1).getName());
        assertEquals("Vortek", result.get(2).getName());
    }

    @Test
    void updateCompanyById_ShouldUpdate_WhenCompanyExists() {
        int id = 1;
        CompanyDto dto = CompanyDto.builder()
                .id(id)
                .name("Steam Ducks Updated")
                .cnpj("12345678000195")
                .contact("newsteamducks@altave.com")
                .build();

        Company existingCompany = new Company();
        existingCompany.setId(id);

        Company updatedEntity = new Company();
        updatedEntity.setId(id);
        updatedEntity.setName(dto.getName());
        updatedEntity.setCnpj(dto.getCnpj());
        updatedEntity.setContact(dto.getContact());

        when(companyRepository.findById(id)).thenReturn(Optional.of(existingCompany));
        when(companyConverter.toEntity(dto)).thenReturn(updatedEntity);

        companyService.updateCompanyById(id, dto);

        verify(companyRepository).save(updatedEntity);
    }

    @Test
    void deleteCompanyById_ShouldDelete_WhenCompanyExists() {
        int id = 1;
        when(companyRepository.existsById(id)).thenReturn(true);

        companyService.deleteCompanyById(id);

        verify(companyRepository).deleteById(id);
    }

    @Test
    void deleteCompanyById_ShouldNotDelete_WhenCompanyDoesNotExist() {
        int id = 99;
        when(companyRepository.existsById(id)).thenReturn(false);

        companyService.deleteCompanyById(id);

        verify(companyRepository, never()).deleteById(id);
    }
}
