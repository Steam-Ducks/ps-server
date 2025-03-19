package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.CompanyPositionEmployee;
import pointsystem.entity.CompanyPositionEmployeeId;

public interface CompanyPositionEmployeeRepository extends JpaRepository<CompanyPositionEmployee, CompanyPositionEmployeeId> {
}
