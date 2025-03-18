package pointsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


@Entity
@Table(name = "cargo")

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cargo_id")
    private int id;

    @Column(name = "cargo_nome", nullable = false)
    private String name;

    @Column(name = "cargo_remuneracao", nullable = false)
    private Float salary;

    @ManyToOne
    @JoinColumn(name = "empresa_ter_id")
    @JsonBackReference("business-roles")
    private Business business;



    public Role() {}

    public Role(int id, String name, Business business, Float salary) {
        this.id = id;
        this.name = name;
        this.business = business;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }
}
