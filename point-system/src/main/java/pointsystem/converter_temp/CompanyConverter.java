package pointsystem.converter_temp;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pointsystem.dto.company.CompanyDto;
import pointsystem.dto.company.CompanyPositionEmployeeDto;
import pointsystem.entity.Company;

import java.util.List;

@Component
public class CompanyConverter implements Converter<Company, CompanyDto> {

    @Autowired
    private ModelMapper modelMapper;

    private TypeMap<Company, CompanyDto> companyToDtoMapper;
    private TypeMap<CompanyDto, Company> dtoToCompanyMapper;

    @Override
    public CompanyDto toDto(Company entity) {
        if (entity == null) {
            return null;
        }

        if (companyToDtoMapper == null) {
            companyToDtoMapper = modelMapper.createTypeMap(Company.class, CompanyDto.class);
            companyToDtoMapper.addMappings(mapper ->
                    mapper.skip(CompanyDto::setEmployees) // Skip employees for custom mapping
            );
        }

        CompanyDto dto = modelMapper.map(entity, CompanyDto.class);

        // Custom mapping for employees
        if (entity.getEmployees() != null) {
            dto.setEmployees(entity.getEmployees().stream()
                    .map(employee -> new CompanyPositionEmployeeDto(employee))
                    .toList());
        }

        return dto;
    }

    @Override
    public Company toEntity(CompanyDto dto) {
        if (dto == null) {
            return null;
        }

        if (dtoToCompanyMapper == null) {
            dtoToCompanyMapper = modelMapper.createTypeMap(CompanyDto.class, Company.class);
            dtoToCompanyMapper.addMappings(mapper ->
                    mapper.skip(Company::setId) // Skip ID for creation
            );
        }

        Company company = modelMapper.map(dto, Company.class);

        // Additional logic if needed for employees or other fields
        return company;
    }

    @Override
    public List<CompanyDto> toDto(List<Company> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<Company> toEntity(List<CompanyDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}