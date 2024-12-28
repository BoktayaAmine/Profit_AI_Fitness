package ma.ensa.project.dto;


import lombok.Data;
import java.util.List;
import ma.ensa.project.model.Exercise;

@Data
public class RecommendationDTO {
    private String textual_recommendations;
    private List<Exercise> exercise_recommendations;
}