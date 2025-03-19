package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyPositionEmployeeId {
    @Column(name = "company_id")
    private int companyId;
    @Column(name = "position_id")
    private int positionId;
    @Column(name = "employee_id")
    private int employeeId;

    public CompanyPositionEmployeeId() {
    }
    public CompanyPositionEmployeeId(int companyId, int positionId, int employeeId) {
        this.companyId = companyId;
        this.positionId = positionId;
        this.employeeId = employeeId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
}
