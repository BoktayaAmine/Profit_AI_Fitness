package ma.ensa.project.service;


import ma.ensa.project.model.Objectif;
import ma.ensa.project.model.User;
import ma.ensa.project.repo.ObjectifRepository;
import ma.ensa.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;



    public User signup(User user) {
        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            User user = userOptional.get();
            user.setImage(null); // Set binary image data to null
            return Optional.of(user);        }
        return Optional.empty();
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Existing updates...
            if (updatedUser.getName() != null) user.setName(updatedUser.getName());
            if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            if (updatedUser.getSexe() != null) user.setSexe(updatedUser.getSexe());
            if (updatedUser.getTaille() != 0) user.setTaille(updatedUser.getTaille());
            if (updatedUser.getPoids() != 0) user.setPoids(updatedUser.getPoids());
            if (updatedUser.getNiveau() != null) user.setNiveau(updatedUser.getNiveau());
            if (updatedUser.getHealthCondition() != null) user.setHealthCondition(updatedUser.getHealthCondition());

            // New image-related updates
            if (updatedUser.getImage_base64() != null) {
                user.setImage_base64(updatedUser.getImage_base64());
                // Convert base64 to byte array and set the image field
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(updatedUser.getImage_base64());
                    user.setImage(imageBytes);
                } catch (IllegalArgumentException e) {
                    // Handle invalid base64 string
                    throw new IllegalArgumentException("Invalid base64 string for image");
                }
            }

            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    // Récupérer un utilisateur par son ID
    public Optional<User> getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setImage(null); // Set binary image data to null
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

}
