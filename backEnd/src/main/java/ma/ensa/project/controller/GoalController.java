package ma.ensa.project.controller;

import ma.ensa.project.model.Goal;
import ma.ensa.project.repo.GoalRepository;
import ma.ensa.project.repo.UserRepository;
import ma.ensa.project.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user/{userId}")
    public ResponseEntity<Goal> createGoal(@PathVariable Long userId, @RequestBody Goal goal) {
        return userRepository.findById(userId).map(user -> {
            goal.setUser (user);
            Goal savedGoal = goalRepository.save(goal);
            return ResponseEntity.ok(savedGoal);
        }).orElseThrow(() -> new ResourceNotFoundException("User  not found with id: " + userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getUserGoals(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User  not found with id: " + userId);
        }
        List<Goal> goals = goalRepository.findByUserId(userId);
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long goalId, @RequestBody Goal goalDetails) {
        return goalRepository.findById(goalId).map(goal -> {
            goal.setTitle(goalDetails.getTitle());
            goal.setDescription(goalDetails.getDescription());
            Goal updatedGoal = goalRepository.save(goal);
            return ResponseEntity.ok(updatedGoal);
        }).orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Object> deleteGoal(@PathVariable Long goalId) {
        return goalRepository.findById(goalId).map(goal -> {
            goalRepository.delete(goal);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
    }

    @PutMapping("/{goalId}/complete")
    public ResponseEntity<Goal> completeGoal(@PathVariable Long goalId) {
        return goalRepository.findById(goalId).map(goal -> {
            goal.setCompleted(true);
            Goal updatedGoal = goalRepository.save(goal);
            return ResponseEntity.ok(updatedGoal);
        }).orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));
    }
}