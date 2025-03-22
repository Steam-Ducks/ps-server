package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
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
    @JsonManagedReference
    private List<CompanyPositionEmployee> employees ;

    public Company() {}

    public Company(int id, String name, String cnpj, String contact) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.contact = contact;
    }


    public int getId() {
        return id;
    }

    public void setId(int companyId) {
        this.id = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<CompanyPositionEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<CompanyPositionEmployee> employees) {
        this.employees = employees;
    }
}
