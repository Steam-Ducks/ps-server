package pointsystem.dto.employee;


public record CreateEmployeeDto(String name, String cpf, String photo, float salary, int companyId, int positionId) {
}
