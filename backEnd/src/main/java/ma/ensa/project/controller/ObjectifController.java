package ma.ensa.project.controller;


import ma.ensa.project.model.Objectif;
import ma.ensa.project.service.ObjectifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/objectifs")
public class ObjectifController {

    @Autowired
    private ObjectifService objectifService;



    @PostMapping("/add/{userId}")
    public ResponseEntity<Objectif> addObjectif(@PathVariable Long userId, @RequestBody Objectif objectif) {
        Objectif newObjectif = objectifService.addObjectif(userId, objectif);
        return ResponseEntity.ok(newObjectif);
    }

    @PutMapping("/{id}/done")
    public ResponseEntity<Objectif> updateDone(@PathVariable Long id, @RequestParam boolean done) {
        Optional<Objectif> objectifOptional = objectifService.getObjectifById(id);
        if (objectifOptional.isPresent()) {
            Objectif objectif = objectifOptional.get();
            objectif.setDone(done);
            objectifService.saveObjectif(objectif);
            return ResponseEntity.ok(objectif);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjectif(@PathVariable Long id) {
        Optional<Objectif> objectifOptional = objectifService.getObjectifById(id);
        if (objectifOptional.isPresent()) {
            objectifService.deleteObjectif(id);
            return ResponseEntity.noContent().build(); // No content to return on successful deletion
        }
        return ResponseEntity.notFound().build(); // Objectif not found
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Objectif>> getAllObjectifsByUserId(@PathVariable Long userId) {
        List<Objectif> objectifs = objectifService.getAllObjectifsByUserId(userId);
        if (objectifs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(objectifs);
    }

}
