package ma.ensa.mobile.profit.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Recommendation {
    @SerializedName("textual_recommendations")
    private String textualRecommendations;

    @SerializedName("exercise_recommendations")
    private List<Exercise> exerciseRecommendations;

    public String getTextualRecommendations() {
        return textualRecommendations;
    }

    public void setTextualRecommendations(String textualRecommendations) {
        this.textualRecommendations = textualRecommendations;
    }

    public List<Exercise> getExerciseRecommendations() {
        return exerciseRecommendations;
    }

    public void setExerciseRecommendations(List<Exercise> exerciseRecommendations) {
        this.exerciseRecommendations = exerciseRecommendations;
    }
}