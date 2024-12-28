package ma.ensa.mobile.profit.services;


import java.util.List;

import ma.ensa.mobile.profit.models.Goal;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GoalService {
    @POST("goals/user/{userId}")
    Call<Goal> createGoal(@Path("userId") Long userId, @Body Goal goal);

    @GET("goals/user/{userId}")
    Call<List<Goal>> getUserGoals(@Path("userId") Long userId);

    @PUT("goals/{goalId}/complete")
    Call<Goal> completeGoal(@Path("goalId") Long goalId);

    @PUT("goals/{goalId}")
    Call<Goal> updateGoal(@Path("goalId") Long goalId, @Body Goal goal);

    @DELETE("goals/{goalId}")
    Call<Void> deleteGoal(@Path("goalId") Long goalId);
}