package ma.ensa.project.service;

import lombok.extern.slf4j.Slf4j;
import ma.ensa.project.dto.FitnessRequestDTO;
import ma.ensa.project.dto.RecommendationDTO;
import ma.ensa.project.exceptions.ResourceNotFoundException;
import ma.ensa.project.model.Exercise;
import ma.ensa.project.model.Goal;
import ma.ensa.project.model.Objectif;
import ma.ensa.project.model.User;
import ma.ensa.project.repo.GoalRepository;
import ma.ensa.project.repo.ObjectifRepository;
import ma.ensa.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FitnessService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectifRepository objectifRepository;

    @Autowired
    private GoalRepository goalRepository;

    private final String FLASK_URL = "http://localhost:5000/api";

    public RecommendationDTO getFitnessRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User  not found with id: " + userId));

        List<Objectif> objectives = objectifRepository.findByUserId(userId);

        FitnessRequestDTO request = createFitnessRequest(user, objectives);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<FitnessRequestDTO> entity = new HttpEntity<>(request, headers);

            ResponseEntity<RecommendationDTO> response = restTemplate.exchange(
                    FLASK_URL + "/fitness",
                    HttpMethod.POST,
                    entity,
                    RecommendationDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to get recommendations from Flask service");
            }

        } catch (Exception e) {
            log.error("Error getting recommendations: ", e);
            throw new RuntimeException("Error processing fitness recommendations", e);
        }
    }


    private FitnessRequestDTO createFitnessRequest(User user, List<Objectif> objectives) {
        FitnessRequestDTO request = new FitnessRequestDTO();

        // Récupérer les goals de l'utilisateur
        List<Goal> userGoals = goalRepository.findByUserId(user.getId());

        // Créer une liste de maps pour les goals
        List<Map<String, String>> goalsData = userGoals.stream()
                .map(goal -> {
                    Map<String, String> goalMap = new HashMap<>();
                    goalMap.put("title", goal.getTitle());
                    goalMap.put("description", goal.getDescription());
                    goalMap.put("completed", String.valueOf(goal.getCompleted()));
                    return goalMap;
                })
                .collect(Collectors.toList());

        // Map user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("sexe", user.getSexe());
        userData.put("taille", user.getTaille());
        userData.put("poids", user.getPoids());
        userData.put("niveau", user.getNiveau());
        userData.put("healthCondition", user.getHealthCondition());
        userData.put("personal_goals", goalsData); // Ajouter les goals dans user_data

        // Map objectives
        List<Objectif> objectiveDTOs = objectives.stream()
                .map(obj -> {
                    Objectif dto = new Objectif();
                    dto.setNom(obj.getNom());
                    dto.setType(obj.getType());
                    dto.setValue(obj.getValue());
                    dto.setDone(obj.isDone());
                    return dto;
                })
                .collect(Collectors.toList());

        request.setUser_data(userData);
        request.setObjectives(objectiveDTOs);

        return request;
    }


public List<Exercise> getRecommendedExercises(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User  not found with id: " + userId));

    List<Objectif> completedObjectives = objectifRepository.findByUserIdAndDone(userId, true);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("user_data", Map.of(
            "niveau", user.getNiveau(),
            "healthCondition", user.getHealthCondition()
    ));
    requestBody.put("completed_exercises",
            completedObjectives.stream()
                    .map(Objectif::getNom)
                    .collect(Collectors.toList())
    );

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, List<Exercise>>> response = restTemplate.exchange(
                FLASK_URL + "/exercises/recommended",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, List<Exercise>>>() {}
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("recommended_exercises");
        } else {
            throw new RuntimeException("Failed to get recommended exercises");
        }

    } catch (Exception e) {
        throw new RuntimeException("Error processing exercise recommendations", e);
    }
}



}