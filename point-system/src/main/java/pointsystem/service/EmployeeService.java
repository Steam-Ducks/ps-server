package pointsystem.service;

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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

        CompanyPositionEmployeeId companyPositionEmployeeId = new CompanyPositionEmployeeId(company.getId(), position.getId(), savedEmployee.getId());
        CompanyPositionEmployee pivotTable = new CompanyPositionEmployee(companyPositionEmployeeId, company, position, savedEmployee, employeeDto.getSalary());

        companyPositionEmployeeRepository.save(pivotTable);
        return savedEmployee.getId();
    }

    public Optional<EmployeeDto> getEmployeeById(int employeeId) {
        return employeeRepository.findById(employeeId)
                .map(employeeConverter::toDto);
    }

    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employeeConverter.toDto(
                employees.stream()
                        .sorted(Comparator.comparing(Employee::getName))
                        .toList()
        );
    }

    public void updateEmployeeById(int employeeId, EmployeeDto employeeDto) {
        Optional<Employee> employeeEntity = employeeRepository.findById(employeeId);
        employeeEntity.ifPresent(employee -> {
            Employee updatedEmployee = employeeConverter.toEntity(employeeDto);
            updatedEmployee.setId(employeeId); // Ensure the ID is preserved
            employeeRepository.save(updatedEmployee);
        });
    }

    public void deleteEmployeeById(int employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
        }
    }

    private boolean isValidCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }
}