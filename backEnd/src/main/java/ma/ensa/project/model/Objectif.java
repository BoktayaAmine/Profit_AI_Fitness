package ma.ensa.project.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@Entity
public class Objectif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom; // Name of the exercise
    private String type; // Type (e.g., "DURATION", "COUNT", "DISTANCE")
    private float value; // User-provided value
    private String image; // Exercise image URL
    private boolean done = false; // New field to track completion status


    @ManyToOne
    @JsonIgnore // To avoid circular references in JSON
    private User user; // Each Objectif belongs to a User



}
