package ma.ensa.project.service;


import ma.ensa.project.model.Objectif;
import ma.ensa.project.model.User;
import ma.ensa.project.repo.ObjectifRepository;
import ma.ensa.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectifService {

    @Autowired
    private ObjectifRepository objectifRepository;

    @Autowired
    private UserRepository userRepository;

    public Objectif addObjectif(Long userId, Objectif objectif) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        objectif.setUser(user);
        return objectifRepository.save(objectif);
    }

    public List<Objectif> getObjectifsByUserId(Long userId) {
        return objectifRepository.findByUserId(userId);
    }

    // New method to retrieve an objectif by ID
    public Optional<Objectif> getObjectifById(Long id) {
        return objectifRepository.findById(id);
    }

    // New method to save/update an objectif
    public Objectif saveObjectif(Objectif objectif) {
        return objectifRepository.save(objectif);
    }

    public void deleteObjectif(Long id) {
        objectifRepository.deleteById(id); // Assuming you're using a JPA repository
    }

    public List<Objectif> getAllObjectifsByUserId(Long userId) {
        return objectifRepository.findByUserId(userId);
    }
}
