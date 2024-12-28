package ma.ensa.project.repo;

import ma.ensa.project.model.Exercise;
import ma.ensa.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    @Query("SELECT e FROM Exercise e ORDER BY CASE e.niveau " +
            "WHEN 'beginner' THEN 1 " +
            "WHEN 'intermediate' THEN 2 " +
            "WHEN 'advanced' THEN 3 " +
            "ELSE 4 END")
    List<Exercise> findAllSorted();
}
