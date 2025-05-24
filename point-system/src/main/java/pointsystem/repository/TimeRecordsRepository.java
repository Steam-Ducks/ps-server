package pointsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pointsystem.entity.TimeRecords;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeRecordsRepository extends JpaRepository<TimeRecords, Long> {

    @Query("SELECT t FROM TimeRecords t WHERE " +
            "t.employee.id = :employeeId " +
            "AND (cast(:startDate as timestamp) IS NULL OR t.dateTime >= :startDate) " +
            "AND (cast(:endDate as timestamp) IS NULL OR t.dateTime <= :endDate) " +
            "ORDER BY t.dateTime")
    List<TimeRecords> findByEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}