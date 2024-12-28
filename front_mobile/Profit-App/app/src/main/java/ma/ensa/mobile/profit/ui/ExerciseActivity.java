package ma.ensa.mobile.profit.ui;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.adapters.ExerciseAdapter;
import ma.ensa.mobile.profit.models.Exercise;
import ma.ensa.mobile.profit.models.Objectif;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ExerciseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // Class-level variable
    private ExerciseAdapter adapter;

    private Long userId;

    private String userNiveau;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        recyclerView = findViewById(R.id.recycler_view);  // Use class-level variable
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user ID is passed
        }

        fetchUserNiveau();

    }

    private void fetchUserNiveau() {
        AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        authService.getUserNiveau(userId).enqueue(new Callback<AuthService.StringResponse>() {
            @Override
            public void onResponse(Call<AuthService.StringResponse> call, Response<AuthService.StringResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userNiveau = response.body().getNiveau();
                    fetchExercises(); // Only fetch exercises after we have the user's niveau
                } else {
                    Toast.makeText(ExerciseActivity.this, "Failed to fetch user niveau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthService.StringResponse> call, Throwable t) {
                Toast.makeText(ExerciseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchExercises() {
        AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
        authService.getExercises().enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ExerciseAdapter(ExerciseActivity.this, response.body(), userNiveau);
                    recyclerView.setAdapter(adapter);  // Set the adapter to the correct RecyclerView
                } else {
                    Toast.makeText(ExerciseActivity.this, "Failed to fetch exercises", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Toast.makeText(ExerciseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void showExerciseDialog(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        // Inflate custom layout
        View dialogView = inflater.inflate(R.layout.dialog_exercise, null);
        builder.setView(dialogView);



        // Get UI elements
        ImageView exerciseImageView = dialogView.findViewById(R.id.dialog_exercise_image);
        TextView nameTextView = dialogView.findViewById(R.id.dialog_exercise_name);
        TextView levelTextView = dialogView.findViewById(R.id.dialog_exercise_level);
        EditText inputField = dialogView.findViewById(R.id.dialog_input_field);
        Button addButton = dialogView.findViewById(R.id.dialog_add_button);

        // Set data
        nameTextView.setText(exercise.getNom());
        levelTextView.setText(exercise.getNiveau());

        // Load the exercise image
        int imageResId = getDrawableResourceId(exercise.getImage());
        if (imageResId != 0) {
            Glide.with(this)
                    .load(imageResId)
                    .into(exerciseImageView);
        } else {
            exerciseImageView.setImageResource(R.drawable.circle_background); // Default image
        }

        // Dynamically set input field hint
        switch (exercise.getType()) {
            case "DURATION":
                inputField.setHint("Duration (min)");
                break;
            case "COUNT":
                inputField.setHint("Count");
                inputField.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "DISTANCE":
                inputField.setHint("Distance (km)");
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        // Add button click listener
        addButton.setOnClickListener(v -> {
            String inputValue = inputField.getText().toString();
            if (inputValue.isEmpty()) {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an Objectif object
            float value = Float.parseFloat(inputValue);
            Objectif objectif = new Objectif(
                    exercise.getNom(),
                    exercise.getType(),
                    value,
                    exercise.getImage()
            );

            // Send the Objectif to the backend
            AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
            authService.addObjectif(userId, objectif).enqueue(new Callback<Objectif>() {
                @Override
                public void onResponse(Call<Objectif> call, Response<Objectif> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ExerciseActivity.this, "Objectif added successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Close the dialog
                    } else {
                        Toast.makeText(ExerciseActivity.this, "Failed to add Objectif", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Objectif> call, Throwable t) {
                    Toast.makeText(ExerciseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });



    }
    private int getDrawableResourceId(String imageName) {
        return getResources().getIdentifier(imageName, "drawable", getPackageName());
    }

}


