package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pointsystem.entity.CompanyPositionEmployee;


import java.util.List;
import java.util.Optional;

public interface CompanyPositionEmployeeRepository extends JpaRepository<CompanyPositionEmployee, Integer> {
    Optional<CompanyPositionEmployee> findByEmployeeId(int id);
    @Query("SELECT cpe.employee.id FROM CompanyPositionEmployee cpe WHERE cpe.company.id = :companyId")
    List<Integer> findByCompanyId(@Param("companyId") int companyId);
}
