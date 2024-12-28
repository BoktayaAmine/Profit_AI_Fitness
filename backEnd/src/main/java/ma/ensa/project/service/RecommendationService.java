package ma.ensa.project.service;

import ma.ensa.project.model.Recommendation;
import ma.ensa.project.repo.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recRepo;

    public Recommendation addRecommandation(Recommendation r) {
        return recRepo.save(r);
    }

}
