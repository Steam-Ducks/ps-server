package pointsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pointsystem.dto.company.CompanyMapper;
import pointsystem.dto.company.CompanyResponseDto;
import pointsystem.dto.company.CreateCompanyDto;
import pointsystem.dto.company.UpdateCompanyDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public int createCompany(CreateCompanyDto createCompanyDto) {
        if (companyRepository.existsByCnpj(createCompanyDto.cnpj())) {
            throw new IllegalArgumentException("CNPJ já está em uso.");
        }

        Company company = CompanyMapper.toEntity(createCompanyDto);
        return companyRepository.save(company).getId();
    }

    @Transactional
    public Optional<CompanyResponseDto> getCompanyById(int companyId) {
        return companyRepository.findById(companyId).map(CompanyMapper::toResponseDto);
    }

    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .sorted(Comparator.comparing(Company::getName))
                .map(CompanyMapper::toResponseDto)
                .toList();
    }

    public void updateCompanyById(int companyId, UpdateCompanyDto updateCompanyDto) {
        Optional<Company> companyEntity = companyRepository.findById(companyId);
        companyEntity.ifPresent(company -> {
            CompanyMapper.updateEntityFromDto(updateCompanyDto, company);
            companyRepository.save(company);
        });
    }

    public void deleteCompanyById(int companyId) {
        if (companyRepository.existsById(companyId)) {
            companyRepository.deleteById(companyId);
        }
    }
}