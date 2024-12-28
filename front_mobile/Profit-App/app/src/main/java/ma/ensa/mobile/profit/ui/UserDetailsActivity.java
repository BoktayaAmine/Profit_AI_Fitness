package ma.ensa.mobile.profit.ui;


import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.User;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsActivity extends AppCompatActivity {
    private EditText heightEditText, weightEditText;
    private Spinner genderSpinner, levelSpinner, conditionSpinner;
    private Button saveButton;

    private AuthService authService;
    private Long userId; // ID de l'utilisateur à mettre à jour

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialiser les vues
        heightEditText = findViewById(R.id.height_edit_text);
        weightEditText = findViewById(R.id.weight_edit_text);
        genderSpinner = findViewById(R.id.gender_spinner);
        levelSpinner = findViewById(R.id.level_spinner);
        conditionSpinner = findViewById(R.id.condition_spinner);
        saveButton = findViewById(R.id.save_button);

        // Configurer les Spinners
        setupSpinners();

        // Récupérer l'ID utilisateur transmis depuis SignupTabFragment
        userId = getIntent().getLongExtra("USER_ID", -1);

        // Initialiser Retrofit
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);

        // Action du bouton enregistrer
        saveButton.setOnClickListener(v -> saveUserDetails());
    }

    private void setupSpinners() {
        // Genre
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Niveau
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(
                this, R.array.level_array, android.R.layout.simple_spinner_item
        );
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);

        // Conditions médicales
        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                this, R.array.condition_array, android.R.layout.simple_spinner_item
        );
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void saveUserDetails() {
        String gender = genderSpinner.getSelectedItem().toString();
        float height = Float.parseFloat(heightEditText.getText().toString());
        float weight = Float.parseFloat(weightEditText.getText().toString());
        String level = levelSpinner.getSelectedItem().toString();
        String condition = conditionSpinner.getSelectedItem().toString();

        // Construire un objet utilisateur
        User updatedUser = new User();
        updatedUser.setSexe(gender);
        updatedUser.setTaille(height);
        updatedUser.setPoids(weight);
        updatedUser.setNiveau(level);
        updatedUser.setHealthCondition(condition);

        // Appel Retrofit pour mettre à jour les données utilisateur
        authService.updateUser(userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UserDetailsActivity.this, "Details saved successfully!", Toast.LENGTH_SHORT).show();
                    // Naviguer vers UserDetailsActivity
                    Intent intent = new Intent(UserDetailsActivity.this, WelcomActivity.class);
                    intent.putExtra("USER_ID", userId); // Transmettre l'ID utilisateur
                    startActivity(intent);
                    finish(); // Terminer l'activité actuelle
                } else {
                    Toast.makeText(UserDetailsActivity.this, "Failed to save details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserDetailsActivity", "Error: " + t.getMessage());
                Toast.makeText(UserDetailsActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

