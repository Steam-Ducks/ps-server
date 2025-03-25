package pointsystem.dto.employee;

public record UpdateEmployeeDto(String name, String cpf, Boolean status, String photo, float salary, int companyId, int positionId) {
}
