package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

}
