package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "companies", uniqueConstraints = @UniqueConstraint(columnNames = "cnpj"))
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "cnpj", unique = true)
    private String cnpj;

    @Column(name = "contact")
    private String contact;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<CompanyPositionEmployee> employees ;

    public Company(int id, String name, String cnpj, String contact) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.contact = contact;
    }


}
