package pointsystem.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pointsystem.controller.CompanyController;
import pointsystem.dto.company.CompanyDto;
import pointsystem.service.CompanyService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyControllerTest {

    private CompanyService companyService;
    private CompanyController companyController;

    @BeforeEach
    void setUp() {
        companyService = mock(CompanyService.class);
        companyController = new CompanyController(companyService);
    }

    @Test
    void testCreateCompanySuccess() {
        CompanyDto dto = new CompanyDto();
        dto.setCnpj("12.345.678/0001-90");
        when(companyService.createCompany(dto)).thenReturn(1);

        ResponseEntity<?> response = companyController.createCompany(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Map.of("id", 1), response.getBody());
    }

    @Test
    void testCreateCompanyInvalidCnpj() {
        CompanyDto dto = new CompanyDto();
        dto.setCnpj("123");

        ResponseEntity<?> response = companyController.createCompany(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("message", "CNPJ/CPF inválido."), response.getBody());
    }

    @Test
    void testCreateCompanyThrowsException() {
        CompanyDto dto = new CompanyDto();
        dto.setCnpj("12.345.678/0001-90");
        when(companyService.createCompany(dto)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = companyController.createCompany(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Map.of("message", "Erro ao criar a empresa. Tente novamente."), response.getBody());
    }

    @Test
    void testGetCompanyByIdFound() {
        CompanyDto company = new CompanyDto();
        when(companyService.getCompanyById(1)).thenReturn(Optional.of(company));

        ResponseEntity<CompanyDto> response = companyController.getCompanyById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(company, response.getBody());
    }

    @Test
    void testGetCompanyByIdNotFound() {
        when(companyService.getCompanyById(1)).thenReturn(Optional.empty());

        ResponseEntity<CompanyDto> response = companyController.getCompanyById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllCompanies() {
        CompanyDto c1 = new CompanyDto();
        CompanyDto c2 = new CompanyDto();
        List<CompanyDto> companies = Arrays.asList(c1, c2);
        when(companyService.getAllCompanies()).thenReturn(companies);

        ResponseEntity<List<CompanyDto>> response = companyController.getAllCompanies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(companies, response.getBody());
    }

    @Test
    void testUpdateCompanyById() {
        CompanyDto dto = new CompanyDto();

        ResponseEntity<Void> response = companyController.updateCompanyById(1, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(companyService).updateCompanyById(1, dto);
    }

    @Test
    void testDeleteCompanyById() {
        ResponseEntity<Void> response = companyController.deleteCompanyById(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(companyService).deleteCompanyById(1);
    }
}

