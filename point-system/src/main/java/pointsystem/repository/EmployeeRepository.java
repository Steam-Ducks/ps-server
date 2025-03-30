package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByCpf(String cpf);
}
