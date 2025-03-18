package pointsystem.dto.employee;

import pointsystem.entity.Business;
import pointsystem.entity.Role;


public record CreateEmployeeDto(String name, String cpf, String picture) {
}
