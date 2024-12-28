package ma.ensa.project.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

import ma.ensa.project.model.Exercise;
import ma.ensa.project.model.Objectif;
import ma.ensa.project.model.Goal;

@Data
public class FitnessRequestDTO {
    private Map<String, Object> user_data;
    private List<Objectif> objectives;
    //private List<Goal> personal_goals;  // Ajout des goals personnels
}