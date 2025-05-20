package pointsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.employee.EmployeeDto;
import pointsystem.service.EmployeeService;
import pointsystem.service.SupabaseStorageService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private final EmployeeService employeeService;
    private final SupabaseStorageService supabaseStorageService;

    public EmployeeController(EmployeeService employeeService, SupabaseStorageService supabaseStorageService) {
        this.employeeService = employeeService;
        this.supabaseStorageService = supabaseStorageService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDto employeeDto) {
        try {
            int employeeId = employeeService.createEmployee(employeeDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", employeeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao criar um novo funcionário. Tente novamente."));
        }
    }

    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> uploadEmployeePhoto(@RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = supabaseStorageService.uploadEmployeePhoto(file);
            return ResponseEntity.ok(Map.of("photoUrl", photoUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro no upload de foto"));
        }
    }
    @PutMapping("/updatePhoto")
    public ResponseEntity<?> updateEmployeePhoto(@RequestParam String photoURL, @RequestParam("file") MultipartFile file) {
        try {
            String updatedPhotoUrl = supabaseStorageService.updateEmployeePhoto(photoURL, file);

            return ResponseEntity.ok(Map.of("photoUrl", updatedPhotoUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro em atualizar a foto"));
        }
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable int employeeId) {
        try {
            Optional<EmployeeDto> employee = employeeService.getEmployeeById(employeeId);
            return employee.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        try {
            List<EmployeeDto> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<EmployeeDto>> getEmployeeByCompanyId(@PathVariable int companyId) {
        try {
            List<EmployeeDto> employees = employeeService.getAllEmployeesFromCompany(companyId);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<?> updateEmployeeById(@PathVariable int employeeId, @RequestBody EmployeeDto employeeDto) {
        try {
            employeeService.updateEmployeeById(employeeId, employeeDto);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{employeeId}/terminate")
    public ResponseEntity<Void> terminateEmployee(@PathVariable int employeeId) {
        try {
            employeeService.terminateEmployee(employeeId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable int employeeId) {
        employeeService.deleteEmployeeById(employeeId);
        return ResponseEntity.noContent().build();
    }
}