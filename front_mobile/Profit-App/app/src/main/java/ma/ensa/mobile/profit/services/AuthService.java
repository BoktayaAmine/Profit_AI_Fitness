package ma.ensa.mobile.profit.services;

import java.util.List;

import ma.ensa.mobile.profit.models.Exercise;
import ma.ensa.mobile.profit.models.Objectif;
import ma.ensa.mobile.profit.models.Recommendation;
import ma.ensa.mobile.profit.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthService {
    @POST("users/login")
    Call<User> login(@Body User user);

    @POST("users/signup")
    Call<User> signup(@Body User user);

    @PUT("users/update/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User updatedUser);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    @GET("users/{userId}/niveau")
    Call<StringResponse> getUserNiveau(@Path("userId") Long userId);

    @GET("exercise")
    Call<List<Exercise>> getExercises();

    @POST("objectifs/add/{userId}")
    Call<Objectif> addObjectif(@Path("userId") Long userId, @Body Objectif objectif);

    @GET("users/{userId}/objectifs")
    Call<List<Objectif>> getObjectifs(@Path("userId") Long userId);

    @DELETE("objectifs/{id}")
    Call<Void> deleteObjectif(@Path("id") Long id);

    @PUT("objectifs/{id}/done")
    Call<Objectif> updateObjectifDone(@Path("id") Long id, @Query("done") boolean done);


    public class StringResponse {
        private String niveau;

        public String getNiveau() {
            return niveau;
        }

        public void setNiveau(String niveau) {
            this.niveau = niveau;
        }
    }

    @GET("recommendations/user/{userId}")
    Call<Recommendation> getRecommendations(@Path("userId") Long userId);

}