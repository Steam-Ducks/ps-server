package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.CompanyPositionEmployee;


import java.util.Optional;

public interface CompanyPositionEmployeeRepository extends JpaRepository<CompanyPositionEmployee, Integer> {
    Optional<CompanyPositionEmployee> findByEmployeeId(int id);
}
