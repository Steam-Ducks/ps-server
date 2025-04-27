package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyPositionEmployeeId {
    @Column(name = "company_id")
    private int companyId;
    @Column(name = "position_id")
    private int positionId;
    @Column(name = "employee_id")
    private int employeeId;

}
