package ma.ensa.project.repo;


import ma.ensa.project.model.Objectif;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjectifRepository extends JpaRepository<Objectif, Long> {
    List<Objectif> findByUserId(Long userId);
    List<Objectif> findByUserIdAndDone(Long userId, boolean done);
}
