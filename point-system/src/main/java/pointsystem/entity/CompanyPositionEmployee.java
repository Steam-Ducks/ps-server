package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "companies_employees_positions")
public class CompanyPositionEmployee {
    @EmbeddedId
    private CompanyPositionEmployeeId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;


    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("positionId")
    @JoinColumn(name = "position_id")
    private Position position;


    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "salary")
    float salary;

    public CompanyPositionEmployee() {
    }

    public CompanyPositionEmployee(CompanyPositionEmployeeId id, Company company, Position position, Employee employee, float salary) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.employee = employee;
        this.salary = salary;
    }

    public CompanyPositionEmployeeId getId() {
        return id;
    }

    public void setId(CompanyPositionEmployeeId id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }
}
