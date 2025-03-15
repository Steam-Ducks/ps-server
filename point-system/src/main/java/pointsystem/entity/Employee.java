package pointsystem.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "funcionario", uniqueConstraints = @UniqueConstraint(columnNames = "func_cpf"))
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "func_id")
    private int id;

    @Column(name = "func_nome", nullable = false)
    private String name;

    @Column(name = "func_cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "func_status", nullable = false)
    private String status;

    @Column(name = "func_data_desligamento")
    @Temporal(TemporalType.DATE)
    private Date terminationDate;

    @ManyToOne
    @JoinColumn(name = "cargo_id", nullable = false)
    private Role role;

    public Employee() {}

    public Employee(int id, String name, String cpf, String status, Date terminationDate, Role role) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.status = status;
        this.terminationDate = terminationDate;
        this.role = role;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

