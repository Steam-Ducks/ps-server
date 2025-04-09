package pointsystem.dto.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pointsystem.entity.Company;
import pointsystem.entity.Position;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Integer id; // Optional for creation
    private String name;
    private String cpf;
    private Boolean status;
    private String photo;
    private LocalDate startDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer companyId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer positionId;
    private Company company; // Optional for creation
    private Position position; // Optional for creation
    private Float salary; // Optional for creation

}