package pointsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointsystem.dto.company.CompanyDto;
import pointsystem.service.CompanyService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody CompanyDto companyDto) {
        try {
            if (!isValidCNPJ(companyDto.getCnpj())) {
                throw new IllegalArgumentException("CNPJ/CPF inválido.");
            }

            int companyId = companyService.createCompany(companyDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", companyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao criar a empresa. Tente novamente."));
        }
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable int companyId) {
        return companyService.getCompanyById(companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<Void> updateCompanyById(@PathVariable int companyId, @RequestBody CompanyDto companyDto) {
        companyService.updateCompanyById(companyId, companyDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompanyById(@PathVariable int companyId) {
        companyService.deleteCompanyById(companyId);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidCNPJ(String cnpj) {
        return cnpj != null && (cnpj.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}") || cnpj.matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}"));
    }
}