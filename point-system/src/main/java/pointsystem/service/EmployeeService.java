package pointsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.converter.EmployeeConverter;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.entity.*;
import pointsystem.repository.CompanyPositionEmployeeRepository;
import pointsystem.repository.CompanyRepository;
import pointsystem.repository.EmployeeRepository;
import pointsystem.repository.PositionRepository;

import java.util.*;
import java.time.ZoneId;
import java.time.LocalDate;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final CompanyPositionEmployeeRepository companyPositionEmployeeRepository;
    private final EmployeeConverter employeeConverter;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           CompanyRepository companyRepository,
                           PositionRepository positionRepository,
                           CompanyPositionEmployeeRepository companyPositionEmployeeRepository,
                           EmployeeConverter employeeConverter) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.positionRepository = positionRepository;
        this.companyPositionEmployeeRepository = companyPositionEmployeeRepository;
        this.employeeConverter = employeeConverter;
    }

    @Transactional
    public int createEmployee(EmployeeDto employeeDto) {
        if (!isValidCPF(employeeDto.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if (employeeRepository.existsByCpf(employeeDto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        Company company = companyRepository.findById(employeeDto.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));

        Position position = positionRepository.findById(employeeDto.getPositionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo não encontrado"));

        employeeDto.setCompany(company);
        employeeDto.setPosition(position);
        employeeDto.setStatus(true);

        Employee employee = employeeConverter.toEntity(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);

        CompanyPositionEmployee pivotTable = new CompanyPositionEmployee(0, company, position, savedEmployee, employeeDto.getSalary());

        companyPositionEmployeeRepository.save(pivotTable);
        return savedEmployee.getId();
    }

    @Transactional
    public Optional<EmployeeDto> getEmployeeById(int employeeId) {
        return employeeRepository.findById(employeeId)
                .map(employeeConverter::toDto);
    }

    @Transactional
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employeeConverter.toDto(
                employees.stream()
                        .filter(employee -> employee.getStatus())
                        .sorted(Comparator.comparing(Employee::getName))
                        .toList()
        );
    }

    @Transactional
    public List<EmployeeDto> getInactivatedEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employeeConverter.toDto(
                employees.stream()
                        .filter(employee -> !employee.getStatus())
                        .sorted(Comparator.comparing(Employee::getName))
                        .toList()
        );
    }

    @Transactional
    public List<Integer> getAllEmployeesFromCompany(int companyId) {
        return companyPositionEmployeeRepository.findByCompanyId(companyId);
    }


    public Map<String, Integer> countEmployeesByMonth(List<Integer> employeeIds, LocalDate firstDay, LocalDate lastDay) {
        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        Map<String, Integer> startDateCount = new HashMap<>();
        employees.stream()
                .filter(employee -> employee.getStartDate() != null)
                .filter(employee -> !employee.getStartDate().isBefore(firstDay) && !employee.getStartDate().isAfter(lastDay))
                .forEach(employee -> {
                    String startDate = employee.getStartDate().toString(); // Convert LocalDate to String
                    startDateCount.put(startDate, startDateCount.getOrDefault(startDate, 0) + 1);
                });

        return startDateCount;
    }

    @Transactional
    public void updateEmployeeById(int employeeId, EmployeeDto employeeDto) {
        Optional<Employee> employeeEntity = employeeRepository.findById(employeeId);
        employeeEntity.ifPresent(employee -> {
            if (employeeDto.getName() != null) {
                employee.setName(employeeDto.getName());
            }
            if (employeeDto.getStatus() != null) {
                employee.setStatus(employeeDto.getStatus());
            }
            if (employeeDto.getPhoto() != null) {
                employee.setPhoto(employeeDto.getPhoto());
            }
            if (employeeDto.getStartDate() != null) {
                employee.setStartDate(employeeDto.getStartDate());
            }

            Optional<CompanyPositionEmployee> companyPositionEmployeeEntity =
                    companyPositionEmployeeRepository.findByEmployeeId(employee.getId());

            companyPositionEmployeeEntity.ifPresent(pivot -> {
                if (employeeDto.getSalary() != null) {
                    pivot.setSalary(employeeDto.getSalary());
                }
                if (employeeDto.getCompanyId() != null) {
                    Company company = companyRepository.findById(employeeDto.getCompanyId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
                    pivot.setCompany(company);
                }
                if (employeeDto.getPositionId() != null) {
                    Position position = positionRepository.findById(employeeDto.getPositionId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo não encontrado"));
                    pivot.setPosition(position);
                }
                companyPositionEmployeeRepository.save(pivot);
            });

            employeeRepository.save(employee);
        });
    }

    @Transactional
    public void terminateEmployee(int employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        employee.setStatus(false);
        employee.setTerminationDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Optional<CompanyPositionEmployee> companyPositionEmployeeEntity =
                companyPositionEmployeeRepository.findByEmployeeId(employeeId);

        companyPositionEmployeeEntity.ifPresent(pivot -> {
            pivot.setCompany(null);
            pivot.setPosition(null);
            pivot.setSalary(0);
            companyPositionEmployeeRepository.save(pivot);
        });

        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployeeById(int employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
        }
    }

    private boolean isValidCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }
}