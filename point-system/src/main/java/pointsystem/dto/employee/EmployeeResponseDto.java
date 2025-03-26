package pointsystem.dto.employee;

import pointsystem.entity.Employee;
import pointsystem.entity.Company;
import pointsystem.entity.Position;
import pointsystem.entity.CompanyPositionEmployee;

public record EmployeeResponseDto(
        int id,
        String name,
        String cpf,
        Boolean status,
        String photo,
        Company company,
        Position position,
        Float salary
) {
    public EmployeeResponseDto(Employee employee, CompanyPositionEmployee companyPosition) {
        this(
                employee.getId(),
                employee.getName(),
                employee.getCpf(),
                employee.getStatus(),
                employee.getPhoto(),
                companyPosition != null ? companyPosition.getCompany() : null,
                companyPosition != null ? companyPosition.getPosition() : null,
                companyPosition != null ? companyPosition.getSalary() : null
        );
    }
}