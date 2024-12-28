package ma.ensa.project.repo;

import ma.ensa.project.model.Recommendation;
import ma.ensa.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUser (User user);
}