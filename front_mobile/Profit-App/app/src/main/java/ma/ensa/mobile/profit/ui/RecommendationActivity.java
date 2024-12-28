package ma.ensa.mobile.profit.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.adapters.RecommendationAdapter;
import ma.ensa.mobile.profit.models.Recommendation;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationActivity extends AppCompatActivity {
    private static final String TAG = "RecommendationActivity";

    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;
    private TextView textualRecommendationsView;
    private RelativeLayout loadingLayout;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_recommendations);
        textualRecommendationsView = findViewById(R.id.textual_recommendations);
        loadingLayout = findViewById(R.id.loading_layout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId != -1) {
            showLoading();
            fetchRecommendations();
        } else {
            Toast.makeText(this, R.string.user_id_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        loadingLayout.startAnimation(fadeIn);
    }

    private void hideLoading() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        loadingLayout.startAnimation(fadeOut);
    }

    private void fetchRecommendations() {
        AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        authService.getRecommendations(userId).enqueue(new Callback<Recommendation>() {
            @Override
            public void onResponse(Call<Recommendation> call, Response<Recommendation> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    Recommendation recommendation = response.body();
                    Log.d(TAG, "Recommendations received successfully");
                    displayRecommendations(recommendation);
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    showError(getString(R.string.error_fetching_recommendations));
                }
            }

            @Override
            public void onFailure(Call<Recommendation> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Error fetching recommendations: " + t.getMessage());
                showError(t.getMessage());
            }
        });
    }

    private void showError(String message) {
        View rootView = findViewById(android.R.id.content);
        com.google.android.material.snackbar.Snackbar.make(
                rootView,
                message,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show();
    }

    private void displayRecommendations(Recommendation recommendation) {
        if (recommendation.getTextualRecommendations() != null) {
            textualRecommendationsView.setText(recommendation.getTextualRecommendations());

            // Fade in animation pour le texte
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(500);
            textualRecommendationsView.startAnimation(fadeIn);
        }

        if (recommendation.getExerciseRecommendations() != null &&
                !recommendation.getExerciseRecommendations().isEmpty()) {
            adapter = new RecommendationAdapter(recommendation.getExerciseRecommendations());
            recyclerView.setAdapter(adapter);

            // Fade in animation pour la RecyclerView
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(500);
            recyclerView.startAnimation(fadeIn);
        } else {
            textualRecommendationsView.setText(getString(R.string.no_exercise_recommendations));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}