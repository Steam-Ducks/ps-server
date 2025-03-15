package pointsystem.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "role")

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cargo_id")
    private Integer id;

    @Column(name = "cargo_nome", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "empresa_ter_id", nullable = false)
    private Business business;

    @Column(name = "cargo_remuneracao", nullable = false, precision = 10, scale = 2)
    private float salary;
}
