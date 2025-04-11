package pointsystem.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Integer id; // Optional for creation
    private String name;
    private String cnpj;
    private String contact;
    private List<CompanyPositionEmployeeDto> employees; // Optional for creation and update
}