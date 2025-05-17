package pointsystem.dto.timeRecords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRecordsDto {
    private Integer id;
    private Boolean isEdit;
    private Timestamp dateTime;
    private Long employeeId;
    private OffsetDateTime updatedAt;
}
