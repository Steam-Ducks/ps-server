package pointsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.employee.CreateEmployeeDto;
import pointsystem.dto.employee.EmployeeResponseDto;
import pointsystem.dto.employee.UpdateEmployeeDto;
import pointsystem.service.EmployeeService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeDto createEmployeeDto)
    {
        try {
            int employeeId = employeeService.createEmployee(createEmployeeDto);
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

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable int employeeId) {
        try {
            Optional<EmployeeResponseDto> employee = employeeService.getEmployeeById(employeeId);
            return employee.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        try {
            List<EmployeeResponseDto> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Void> updateEmployeeById(@PathVariable int employeeId, @RequestBody UpdateEmployeeDto updateEmployeeDto) {
        employeeService.updateEmployeeById(employeeId, updateEmployeeDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable int employeeId) {
        employeeService.deleteEmployeeById(employeeId);
        return ResponseEntity.noContent().build();
    }
}
