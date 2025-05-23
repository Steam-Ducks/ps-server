package pointsystem.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pointsystem.controller.DashboardController;
import pointsystem.service.CompanyService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardControllerTest {

    private CompanyService companyService;
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        companyService = mock(CompanyService.class);
        dashboardController = new DashboardController(companyService);
    }

    @Test
    void testGetCompaniesDashboardSuccess() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        List<Map<String, Object>> expectedData = List.of(
                Map.of("companyId", 1, "totalHours", 160),
                Map.of("companyId", 2, "totalHours", 120)
        );
        when(companyService.getAllCompanyDashboardData(startDate, endDate)).thenReturn(expectedData);

        ResponseEntity<?> response = dashboardController.getCompaniesDashboard(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedData, response.getBody());
    }

    @Test
    void testGetCompaniesDashboardThrowsException() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        when(companyService.getAllCompanyDashboardData(startDate, endDate)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = dashboardController.getCompaniesDashboard(startDate, endDate);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Map.of("message", "Erro ao obter os dados do dashboard das empresas."), response.getBody());
    }
}
