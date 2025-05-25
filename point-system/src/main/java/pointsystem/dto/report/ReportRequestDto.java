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
    private String companyName;
    private Integer employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String fileFormat;
}
