package pointsystem.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pointsystem.dto.employee.CreateEmployeeDto;
import pointsystem.dto.employee.UpdateEmployeeDto;
import pointsystem.entity.Employee;
import pointsystem.service.EmployeeService;

import java.net.URI;
import java.util.List;
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
    public ResponseEntity<Integer> createEmployee(@RequestBody CreateEmployeeDto createEmployeeDto)
    {
        int employeeId = employeeService.createEmployee(createEmployeeDto);
        return ResponseEntity.created(URI.create("/api/employees/" + employeeId)).build();
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int employeeId) {
        try {
            Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
            if (employee.isPresent()) {
                return ResponseEntity.ok(employee.get());
            }
            else
            {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
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
