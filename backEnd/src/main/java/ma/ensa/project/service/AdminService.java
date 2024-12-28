package ma.ensa.project.service;

import ma.ensa.project.model.Admin;
import ma.ensa.project.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public Optional<Admin> login(String email, String password) {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent() && passwordEncoder.matches(password, adminOptional.get().getPassword())) {
            Admin admin = adminOptional.get();
            admin.setImage(null); // Set binary image data to null for response
            return Optional.of(admin);
        }
        return Optional.empty();
    }

    public Optional<Admin> updateAdmin(Long id, Admin updatedAdmin) {
        Optional<Admin> existingAdmin = adminRepository.findById(id);
        if (existingAdmin.isPresent()) {
            Admin admin = existingAdmin.get();

            if (updatedAdmin.getName() != null) admin.setName(updatedAdmin.getName());
            if (updatedAdmin.getEmail() != null) admin.setEmail(updatedAdmin.getEmail());
            if (updatedAdmin.getPassword() != null) {
                admin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
            }

            // Handle image update
            if (updatedAdmin.getImage_base64() != null) {
                admin.setImage_base64(updatedAdmin.getImage_base64());
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(updatedAdmin.getImage_base64());
                    admin.setImage(imageBytes);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid base64 string for image");
                }
            }

            adminRepository.save(admin);
            return Optional.of(admin);
        }
        return Optional.empty();
    }

    public Optional<Admin> getAdminById(Long id) {
        Optional<Admin> adminOptional = adminRepository.findById(id);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            admin.setImage(null); // Set binary image data to null for response
            return Optional.of(admin);
        }
        return Optional.empty();
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        // Clear binary image data for all admins in the response
        admins.forEach(admin -> admin.setImage(null));
        return admins;
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    public Admin addAdmin(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        admin.setJoinedDate(new Date());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }
}
