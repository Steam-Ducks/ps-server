package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.employee.CreateEmployeeDto;
import pointsystem.dto.employee.UpdateEmployeeDto;
import pointsystem.entity.*;
import pointsystem.repository.CompanyPositionEmployeeRepository;
import pointsystem.repository.CompanyRepository;
import pointsystem.repository.EmployeeRepository;
import pointsystem.repository.PositionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final CompanyPositionEmployeeRepository companyPositionEmployeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, CompanyRepository companyRepository, PositionRepository positionRepository, CompanyPositionEmployeeRepository companyPositionEmployeeRepository) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.positionRepository = positionRepository;
        this.companyPositionEmployeeRepository = companyPositionEmployeeRepository;
    }

    public int createEmployee(CreateEmployeeDto createEmployeeDto) {
        Company company = companyRepository.findById(createEmployeeDto.companyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        Position position = positionRepository.findById(createEmployeeDto.positionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found"));

        Employee employee = new Employee(0, createEmployeeDto.name(), createEmployeeDto.cpf(), true, null, createEmployeeDto.photo());
        Employee savedEmployee = employeeRepository.save(employee);

        CompanyPositionEmployeeId companyPositionEmployeeId = new CompanyPositionEmployeeId(company.getId(), position.getId(), savedEmployee.getId());
        CompanyPositionEmployee pivotTable = new CompanyPositionEmployee(companyPositionEmployeeId, company, position, savedEmployee, createEmployeeDto.salary());

        companyPositionEmployeeRepository.save(pivotTable);
        return savedEmployee.getId();
    }

    public Optional<Employee> getEmployeeById(int employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public void updateEmployeeById(int employeeId, UpdateEmployeeDto updateEmployeeDto) {
        Optional<Employee> employeeEntity = employeeRepository.findById(employeeId);
        employeeEntity.ifPresent(employee -> {
            if (updateEmployeeDto.name() != null) employee.setName(updateEmployeeDto.name());
            if (updateEmployeeDto.cpf() != null) employee.setCpf(updateEmployeeDto.cpf());
            if (updateEmployeeDto.status() != null) employee.setStatus(updateEmployeeDto.status());
            if (updateEmployeeDto.photo() != null) employee.setPhoto(updateEmployeeDto.photo());
            employeeRepository.save(employee);
        });
    }

    public void deleteEmployeeById(int employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
        }
    }
}
