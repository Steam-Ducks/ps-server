package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.employee.CreateEmployeeDto;
import pointsystem.entity.Business;
import pointsystem.entity.Employee;
import pointsystem.entity.Role;
import pointsystem.repository.BusinessRepository;
import pointsystem.repository.EmployeeRepository;
import pointsystem.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BusinessRepository businessRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, BusinessRepository businessRepository, RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.businessRepository = businessRepository;
        this.roleRepository = roleRepository;
    }

    public int createEmployee(Integer businessId, Integer roleId, CreateEmployeeDto createEmployeeDto) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        Employee employee = new Employee(0, createEmployeeDto.name(), createEmployeeDto.cpf(), "ativo", null, business, role, createEmployeeDto.picture());

        Employee savedEmployee = employeeRepository.save(employee);
        return savedEmployee.getId();
    }

    public Optional<Employee> getEmployeeById(int employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
