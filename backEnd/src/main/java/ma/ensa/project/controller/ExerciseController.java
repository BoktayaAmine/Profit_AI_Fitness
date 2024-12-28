package ma.ensa.project.controller;


import ma.ensa.project.model.Exercise;
import ma.ensa.project.model.User;
import ma.ensa.project.service.ExerciseService;
import ma.ensa.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping()
    public ResponseEntity<?> createExercise(@RequestBody Exercise exercise) {
        try {
            Exercise savedUser = exerciseService.CreateExercise(exercise);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        try {
            List<Exercise> exercises = exerciseService.getAllExercises();
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @RequestBody Exercise exercise) {
        try {
            Exercise updatedExercise = exerciseService.updateExercise(id, exercise);
            return ResponseEntity.ok(updatedExercise);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not update exercise");
        }
    }

    // Bulk update exercises
    @PutMapping("/bulk")
    public ResponseEntity<?> updateExercises(@RequestBody List<Exercise> exercises) {
        try {
            List<Exercise> updatedExercises = exerciseService.updateExercises(exercises);
            return ResponseEntity.ok(updatedExercises);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not update exercises");
        }
    }



}
