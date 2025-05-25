package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pointsystem.entity.Company;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    boolean existsByCnpj(String cnpj);

    @Query("SELECT c.id FROM Company c")
    List<Integer> findAllCompanyIds();
}