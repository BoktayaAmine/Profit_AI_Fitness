package ma.ensa.project.service;


import ma.ensa.project.model.Exercise;
import ma.ensa.project.model.User;
import ma.ensa.project.repo.ExerciseRepository;
import ma.ensa.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;


    public Exercise CreateExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAllSorted();
    }

    public Exercise updateExercise(Long id, Exercise updatedExercise) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        if (optionalExercise.isPresent()) {
            Exercise exercise = optionalExercise.get();
            exercise.setNom(updatedExercise.getNom());
            exercise.setNiveau(updatedExercise.getNiveau());
            exercise.setImage(updatedExercise.getImage());
            exercise.setType(updatedExercise.getType());
            return exerciseRepository.save(exercise);
        }
        throw new RuntimeException("Exercise not found");
    }

    public List<Exercise> updateExercises(List<Exercise> exercises) {
        for (Exercise exercise : exercises) {
            Optional<Exercise> optionalExercise = exerciseRepository.findById(exercise.getId());
            if (optionalExercise.isPresent()) {
                Exercise existingExercise = optionalExercise.get();
                existingExercise.setNom(exercise.getNom());
                existingExercise.setNiveau(exercise.getNiveau());
                existingExercise.setImage(exercise.getImage());
                existingExercise.setType(exercise.getType());
                exerciseRepository.save(existingExercise);
            }
        }
        return exerciseRepository.findAll();
    }

}
