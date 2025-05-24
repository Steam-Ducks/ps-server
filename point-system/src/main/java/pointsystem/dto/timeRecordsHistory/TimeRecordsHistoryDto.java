package pointsystem.dto.timeRecordsHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRecordsHistoryDto {
        private Long id;
        private Long timeRecordsId;
        private String username;
        private Timestamp dateTimeBefore;
        private Timestamp dateTimeAfter;
        private LocalDateTime createdAt;
}
