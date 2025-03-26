package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.CompanyPositionEmployee;
import pointsystem.entity.CompanyPositionEmployeeId;

import java.util.Optional;

public interface CompanyPositionEmployeeRepository extends JpaRepository<CompanyPositionEmployee, CompanyPositionEmployeeId> {
    Optional<CompanyPositionEmployee> findByEmployeeId(int id);
}
