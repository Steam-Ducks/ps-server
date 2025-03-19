package pointsystem.dto.company;

import pointsystem.entity.Company;

import java.util.List;

public record CompanyResponseDto(int id, String name, String cnpj, String contact, List<CompanyPositionEmployeeDto> employees) {
    public CompanyResponseDto(Company company)
    {
        this(
                company.getId(),
                company.getName(),
                company.getCnpj(),
                company.getContact(),
                company.getEmployees().stream().map(CompanyPositionEmployeeDto::new).toList());
    }
}
