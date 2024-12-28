package ma.ensa.project.controller;


import ma.ensa.project.model.Objectif;
import ma.ensa.project.model.User;
import ma.ensa.project.service.EmailService;
import ma.ensa.project.service.ObjectifService;
import ma.ensa.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectifService objectifService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            User savedUser = userService.signup(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword())
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> user = userService.updateUser(id, updatedUser);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/niveau")
    public ResponseEntity<Map<String, String>> getUserNiveau(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("niveau", user.get().getNiveau());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}/objectifs")
    public ResponseEntity<List<Objectif>> getObjectifsByUserId(@PathVariable Long userId) {
        List<Objectif> objectifs = objectifService.getObjectifsByUserId(userId);
        return ResponseEntity.ok(objectifs);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            // Save the user

            // Send email with username and password
            String emailContent = "<p>Hello " + user.getName() + ",</p>" +
                    "<p>Your account has been created successfully. Below are your credentials:</p>" +
                    "<p><strong>Email:</strong> " + user.getEmail() + "</p>" +
                    "<p><strong>Password:</strong> " + user.getPassword() + "</p>" +
                    "<p>Please use these credentials to log in to your account.</p>";

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userService.addUser(user);


            emailService.sendEmail(savedUser.getEmail(), "Your Account Has Been Created", emailContent);

            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/bulk-add")
    public ResponseEntity<?> bulkAddUsers(@RequestBody List<User> users) {
        try {
            List<User> savedUsers = new ArrayList<>();
            for (User user : users) {
                // Encode password
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                // Save each user
                User savedUser = userService.addUser(user);
                savedUsers.add(savedUser);
            }
            return ResponseEntity.ok(savedUsers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating users: " + e.getMessage());
        }
    }


}
