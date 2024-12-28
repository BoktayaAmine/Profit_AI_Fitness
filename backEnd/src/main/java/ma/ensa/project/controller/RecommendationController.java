package ma.ensa.project.controller;

import lombok.extern.slf4j.Slf4j;
import ma.ensa.project.dto.RecommendationDTO;
import ma.ensa.project.exceptions.ResourceNotFoundException;
import ma.ensa.project.model.Exercise;
import ma.ensa.project.model.Recommendation;
import ma.ensa.project.model.User;
import ma.ensa.project.repo.UserRepository;
import ma.ensa.project.service.FitnessService;
import ma.ensa.project.service.RecommendationService;
import ma.ensa.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/recommendations")
@Slf4j
public class RecommendationController {

    @Autowired
    private FitnessService fitnessService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;



    @GetMapping("/user/{userId}")
    public ResponseEntity<RecommendationDTO> getRecommendations(@PathVariable Long userId) {
        try {
            RecommendationDTO recommendations = fitnessService.getFitnessRecommendations(userId);

            // My Update : to save recommandation
            Recommendation newRC  = new Recommendation();
            Optional<User> existingUser = userRepository.findById(userId);
            newRC.setUser(existingUser.get());
            newRC.setResponse(recommendations.getTextual_recommendations());
            Recommendation addrc = recommendationService.addRecommandation(newRC);

            return ResponseEntity.ok(recommendations);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting recommendations for user {}: ", userId, e);
            throw new RuntimeException("Failed to get recommendations", e);
        }
    }

    @GetMapping("/exercises/{userId}")
    public ResponseEntity<List<Exercise>> getRecommendedExercises(@PathVariable Long userId) {
        try {
            List<Exercise> exercises = fitnessService.getRecommendedExercises(userId);
            return ResponseEntity.ok(exercises);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting recommended exercises for user {}: ", userId, e);
            throw new RuntimeException("Failed to get recommended exercises", e);
        }
    }
}
