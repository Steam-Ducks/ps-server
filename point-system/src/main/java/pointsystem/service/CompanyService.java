package pointsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pointsystem.converter.CompanyConverter;
import pointsystem.dto.company.CompanyDto;
import pointsystem.entity.Company;
import pointsystem.repository.CompanyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyConverter companyConverter;

    public CompanyService(CompanyRepository companyRepository, CompanyConverter companyConverter) {
        this.companyRepository = companyRepository;
        this.companyConverter = companyConverter;
    }

    @Transactional
    public int createCompany(CompanyDto companyDto) {
        if (companyRepository.existsByCnpj(companyDto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já está em uso.");
        }

        Company company = companyConverter.toEntity(companyDto);
        return companyRepository.save(company).getId();
    }

    @Transactional
    public Optional<CompanyDto> getCompanyById(int companyId) {
        return companyRepository.findById(companyId).map(companyConverter::toDto);
    }

    @Transactional
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .sorted(Comparator.comparing(Company::getName))
                .map(companyConverter::toDto)
                .toList();
    }

    @Transactional
    public void updateCompanyById(int companyId, CompanyDto companyDto) {
        Optional<Company> companyEntity = companyRepository.findById(companyId);
        companyEntity.ifPresent(company -> {
            Company updatedCompany = companyConverter.toEntity(companyDto);
            updatedCompany.setId(companyId);
            companyRepository.save(updatedCompany);
        });
    }

    @Transactional
    public void deleteCompanyById(int companyId) {
        if (companyRepository.existsById(companyId)) {
            companyRepository.deleteById(companyId);
        }
    }
}