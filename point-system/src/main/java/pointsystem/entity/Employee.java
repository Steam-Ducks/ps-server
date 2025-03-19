package pointsystem.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(columnNames = "cpf"))
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "termination_date")
    @Temporal(TemporalType.DATE)
    private Date terminationDate;

    @Column(name = "photo")
    private String photo;


    public Employee() {}

    public Employee(int id, String name, String cpf, Boolean status, Date terminationDate, String photo) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.status = status;
        this.terminationDate = terminationDate;
        this.photo = photo;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}

