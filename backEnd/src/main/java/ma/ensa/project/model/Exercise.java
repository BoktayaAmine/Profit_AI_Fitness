package ma.ensa.project.model;


import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String niveau;

    @Column(nullable = false)
    private String image;

    private String type;

}
