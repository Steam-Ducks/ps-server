package pointsystem.dto.company;
import pointsystem.entity.Company;

public class CompanyMapper {

    public static Company toEntity(CreateCompanyDto createCompanyDto) {
        return new Company(
                0,
                createCompanyDto.name(),
                createCompanyDto.cnpj(),
                createCompanyDto.contact()
        );
    }

    public static CompanyResponseDto toResponseDto(Company company) {
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getCnpj(),
                company.getContact(),
                company.getEmployees().stream().map(CompanyPositionEmployeeDto::new).toList()
        );
    }

    public static void updateEntityFromDto(UpdateCompanyDto updateCompanyDto, Company company) {
        if (updateCompanyDto.name() != null) {
            company.setName(updateCompanyDto.name());
        }
        if (updateCompanyDto.cnpj() != null) {
            company.setCnpj(updateCompanyDto.cnpj());
        }
        if (updateCompanyDto.contact() != null) {
            company.setContact(updateCompanyDto.contact());
        }
    }
}