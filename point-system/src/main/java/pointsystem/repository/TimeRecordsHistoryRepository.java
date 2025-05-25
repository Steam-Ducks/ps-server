package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pointsystem.entity.TimeRecords;
import pointsystem.entity.TimeRecordsHistory;

import java.util.List;

@Repository
public interface TimeRecordsHistoryRepository extends JpaRepository<TimeRecordsHistory, Long> {
    @Query("SELECT h FROM TimeRecordsHistory h WHERE h.timeRecords.id = :timeRecordsId")
    List<TimeRecordsHistory> findHistoryByTimeRecordsId(@Param("timeRecordsId") int timeRecordsId);

    // Novo método para deletar todos os históricos relacionados a um TimeRecords
    void deleteAllByTimeRecords(TimeRecords timeRecords);
}

