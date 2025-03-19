package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}