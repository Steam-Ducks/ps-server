package pointsystem.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.entity.CompanyPositionEmployee;
import pointsystem.entity.Employee;
import pointsystem.repository.CompanyPositionEmployeeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class EmployeeConverter implements Converter<Employee, EmployeeDto> {

    @Autowired
    private ModelMapper modelMapper;

    private final CompanyPositionEmployeeRepository companyPositionEmployeeRepository;

    private TypeMap<EmployeeDto, Employee> employeeMapper;

    @Autowired
    public EmployeeConverter(CompanyPositionEmployeeRepository companyPositionEmployeeRepository) {
        this.companyPositionEmployeeRepository = companyPositionEmployeeRepository;
    }

    @Override
    public EmployeeDto toDto(Employee entity) {
        if (entity == null) {
            return null;
        }

        Optional<CompanyPositionEmployee> companyPosition = companyPositionEmployeeRepository.findByEmployeeId(entity.getId());

        EmployeeDto dto = modelMapper.map(entity, EmployeeDto.class);
        companyPosition.ifPresent(position -> {
            dto.setCompany(position.getCompany());
            dto.setPosition(position.getPosition());
            dto.setSalary(position.getSalary());
        });

        return dto;
    }

    @Override
    public Employee toEntity(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }

        if (employeeMapper == null) {
            employeeMapper = modelMapper.createTypeMap(EmployeeDto.class, Employee.class);
            employeeMapper.addMappings(mapper -> mapper.skip(Employee::setId));
        }

        Provider<Employee> employeeProvider = p -> new Employee();
        employeeMapper.setProvider(employeeProvider);

        Employee employee = modelMapper.map(dto, Employee.class);

        if (dto.getStartDate() == null) {
            employee.setStartDate(LocalDate.now());
        }

        if (dto.getCompany() != null || dto.getPosition() != null || dto.getSalary() != null) {
            CompanyPositionEmployee companyPosition = new CompanyPositionEmployee();
            companyPosition.setCompany(dto.getCompany());
            companyPosition.setPosition(dto.getPosition());
            companyPosition.setSalary(dto.getSalary() != null ? dto.getSalary() : 0.0f);
            companyPosition.setEmployee(employee);
        }

        return employee;
    }

    @Override
    public List<EmployeeDto> toDto(List<Employee> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<Employee> toEntity(List<EmployeeDto> dtos) {
        return modelMapper.map(dtos, new TypeToken<List<Employee>>() {}.getType());
    }
}