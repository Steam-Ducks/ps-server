package pointsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pointsystem.converter.CompanyConverter;
import pointsystem.dto.company.CompanyDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyConverter companyConverter;
    private final EmployeeService employeeService;
    private final TimeRecordsService timeRecordsService;

    public CompanyService(CompanyRepository companyRepository, CompanyConverter companyConverter, EmployeeService employeeService, TimeRecordsService timeRecordsService) {
        this.companyRepository = companyRepository;
        this.companyConverter = companyConverter;
        this.employeeService = employeeService;
        this.timeRecordsService = timeRecordsService;
    }

    @Transactional
    public int createCompany(CompanyDto companyDto) {
        if (companyRepository.existsByCnpj(companyDto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já está em uso.");
        }

        Company company = companyConverter.toEntity(companyDto);
        return companyRepository.save(company).getId();
    }

    @Transactional
    public Optional<CompanyDto> getCompanyById(int companyId) {
        return companyRepository.findById(companyId).map(companyConverter::toDto);
    }

    @Transactional
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .sorted(Comparator.comparing(Company::getName))
                .map(companyConverter::toDto)
                .toList();
    }

    @Transactional
    public void updateCompanyById(int companyId, CompanyDto companyDto) {
        Optional<Company> companyEntity = companyRepository.findById(companyId);
        companyEntity.ifPresent(company -> {
            Company updatedCompany = companyConverter.toEntity(companyDto);
            updatedCompany.setId(companyId);
            companyRepository.save(updatedCompany);
        });
    }

    @Transactional
    public void deleteCompanyById(int companyId) {
        if (companyRepository.existsById(companyId)) {
            companyRepository.deleteById(companyId);
        }
    }

    @Transactional
    public List<Map<String, Object>> getAllCompanyDashboardData(String startDate, String endDate) {
        List<Integer> companyIds = companyRepository.findAllCompanyIds();
        //threads for parallel processing, many calculations required
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<CompletableFuture<Map<String, Object>>> futures = companyIds.stream()
                .map(companyId -> CompletableFuture.supplyAsync(() -> {
                    List<Integer> employeesIds = employeeService.getAllEmployeesFromCompany(companyId);

                    LocalDateTime firstDay = LocalDate.parse(startDate).atStartOfDay();

                    LocalDateTime lastDay = LocalDate.parse(endDate).atTime(23, 59, 59);

                    // time between start and end is the period for the query
                    Duration period = Duration.between(firstDay, lastDay);

                    LocalDateTime lastPeriodStart = firstDay.minus(period);
                    LocalDateTime lastPeriodEnd = lastDay.minus(period);

                    Map<String, Double> currentPeriodData = timeRecordsService.calculateTotalSalaryByCompanyId(companyId, firstDay, lastDay);
                    Map<String, Double> lastPeriodData = timeRecordsService.calculateTotalSalaryByCompanyId(companyId, lastPeriodStart, lastPeriodEnd);

                    double totalSalary = currentPeriodData.get("totalSalary");
                    double totalWorkedHours = currentPeriodData.get("totalWorkedHours");

                    double totalSalaryLastPeriod = lastPeriodData.get("totalSalary");
                    double totalWorkedHoursLastPeriod = lastPeriodData.get("totalWorkedHours");

                    int totalEmployees = employeesIds.size();
                    int newEmployees = employeeService.countEmployeesByMonth(employeesIds, firstDay.toLocalDate(), lastDay.toLocalDate());

                    Map<String, Object> companyData = new HashMap<>();
                    companyData.put("companyId", companyId);
                    companyData.put("totalEmployees", totalEmployees);
                    companyData.put("totalWorkedHours", totalWorkedHours);
                    companyData.put("totalSalary", totalSalary);
                    companyData.put("totalSalaryLastPeriod", totalSalaryLastPeriod);
                    companyData.put("newEmployees", newEmployees);

                    return companyData;
                }, executor))
                .toList();

        List<Map<String, Object>> dashboardDataList = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        executor.shutdown();
        return dashboardDataList;
    }
}