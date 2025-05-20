package pointsystem.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pointsystem.service.CompanyService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CompanyService companyService;

    public DashboardController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<?> getCompaniesDashboard(
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            List<Map<String, Object>> dashboardData = companyService.getAllCompanyDashboardData(startDate, endDate);
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao obter os dados do dashboard das empresas."));
        }
    }
}
