package pointsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pointsystem.dto.company.CompanyResponseDto;
import pointsystem.dto.company.CreateCompanyDto;
import pointsystem.dto.company.UpdateCompanyDto;
import pointsystem.service.CompanyService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/companies")
@Tag(name = "Company API", description = "Operations related to companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "Create a new company", description = "Creates a company with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid CNPJ/CPF format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody CreateCompanyDto createCompanyDto) {
        try {
            if (!isValidCNPJ(createCompanyDto.cnpj())) {
                throw new IllegalArgumentException("CNPJ/CPF inválido.");
            }
            int companyId = companyService.createCompany(createCompanyDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", companyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro ao criar a empresa. Tente novamente."));
        }
    }

    @Operation(summary = "Get company by ID", description = "Retrieves a company by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable int companyId) {
        return companyService.getCompanyById(companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all companies", description = "Retrieves a list of all companies")
    @ApiResponse(responseCode = "200", description = "List of companies retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(summary = "Update company by ID", description = "Updates the details of an existing company")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PutMapping("/{companyId}")
    public ResponseEntity<Void> updateCompanyById(@PathVariable int companyId, @RequestBody UpdateCompanyDto updateCompanyDto) {
        companyService.updateCompanyById(companyId, updateCompanyDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete company by ID", description = "Deletes a company by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompanyById(@PathVariable int companyId) {
        companyService.deleteCompanyById(companyId);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidCNPJ(String cnpj) {
        return cnpj != null && (cnpj.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}") || cnpj.matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}"));
    }
}
