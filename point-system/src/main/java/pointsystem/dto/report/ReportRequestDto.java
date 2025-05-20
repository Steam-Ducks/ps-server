package pointsystem.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private String reportType;
    private Integer companyId;
    private Integer employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
}
