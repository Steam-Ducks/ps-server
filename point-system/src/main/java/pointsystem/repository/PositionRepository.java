package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Integer> {
}
