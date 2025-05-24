package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointsystem.entity.TimeRecordsHistory;

public interface TimeRecordsHistoryRepository extends JpaRepository<TimeRecordsHistory, Long> {
}
