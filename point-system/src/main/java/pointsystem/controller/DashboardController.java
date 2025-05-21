package pointsystem.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointsystem.dto.report.ReportRequestDto;
import pointsystem.service.CompanyService;
import pointsystem.service.ReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CompanyService companyService;
    private final ReportService reportService;

    public DashboardController(CompanyService companyService, ReportService reportService) {
        this.companyService = companyService;
        this.reportService = reportService;
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

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportReport(@RequestBody ReportRequestDto reportRequestDto) {
        try {
            byte[] excelData;
            String filename;

            switch (reportRequestDto.getReportType()) {

                case "all-companies":
                    excelData = reportService.generateAllCompaniesReport();
                    filename = "relatorio-empresas.xlsx";
                    break;

                case "employee-list":
                    excelData = reportService.generateEmployeeListReport(reportRequestDto.getCompanyId());
                    filename = "relatorio-funcionarios.xlsx";
                    break;

                case "time-record":
                    excelData = reportService.generateTimeRecordReport(
                            reportRequestDto.getEmployeeId(),
                            reportRequestDto.getStartDate(),
                            reportRequestDto.getEndDate()
                    );
                    filename = "relatorio-pontos.xlsx";
                    break;

                case "company-hours":
                    excelData = reportService.generateCompanyHoursReport(
                            reportRequestDto.getCompanyId(),
                            reportRequestDto.getStartDate(),
                            reportRequestDto.getEndDate()
                    );
                    filename = "relatorio-horas-empresa.xlsx";
                    break;

                default:
                    throw new IllegalArgumentException("Invalid report type: " + reportRequestDto.getReportType());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
