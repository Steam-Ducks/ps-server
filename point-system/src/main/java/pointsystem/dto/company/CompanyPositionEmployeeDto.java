package pointsystem.dto.company;

import pointsystem.entity.CompanyPositionEmployee;

public record CompanyPositionEmployeeDto(int positionId, int employeeId, float salary) {
    public CompanyPositionEmployeeDto(CompanyPositionEmployee entity)
    {
        this(entity.getPosition().getId(), entity.getEmployee().getId(), entity.getSalary());
    }
}
