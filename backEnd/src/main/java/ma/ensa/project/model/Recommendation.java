package ma.ensa.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "recommendation_objectif",
            joinColumns = @JoinColumn(name = "recommendation_id"),
            inverseJoinColumns = @JoinColumn(name = "objectif_id")
    )
    private List<Objectif> objectifs;


    public Recommendation() {
    }

    public Recommendation(String response, User user, List<Objectif> objectifs) {
        this.response = response;
        this.user = user;
        this.objectifs = objectifs;
    }
}