package ma.ensa.mobile.profit.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.User;
import ma.ensa.mobile.profit.services.AuthService;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private EditText editName, editEmail, editTaille, editPoids;
    private Spinner editSexe, editNiveau, editHealthCondition;
    private ImageView profileImage;
    private Button btnUpdate, btnSelectImage;
    private String currentImageBase64 = null;
    private AuthService authService;
    private Long userId;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }


        initializeViews();
        setupImagePicker();
        loadUserData(userId);
        setupUpdateButton();
    }

    private void initializeViews() {
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editTaille = findViewById(R.id.editTaille);
        editPoids = findViewById(R.id.editPoids);
        editSexe = findViewById(R.id.editSexe);
        editNiveau = findViewById(R.id.editNiveau);
        editHealthCondition = findViewById(R.id.editHealthCondition);
        profileImage = findViewById(R.id.profileImage);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);

        setupSpinners();

        btnSelectImage.setOnClickListener(v -> openImagePicker());
    }

    private void setupSpinners() {
        // Setup gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSexe.setAdapter(genderAdapter);

        // Setup fitness level spinner
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(
                this, R.array.level_array, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editNiveau.setAdapter(levelAdapter);

        // Setup health condition spinner
        ArrayAdapter<CharSequence> healthConditionAdapter = ArrayAdapter.createFromResource(
                this, R.array.condition_array, android.R.layout.simple_spinner_item);
        healthConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editHealthCondition.setAdapter(healthConditionAdapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            profileImage.setImageBitmap(bitmap);
                            currentImageBase64 = bitmapToBase64(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private void loadUserData(Long userId) {
        authService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    populateFields(user);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(User user) {
        editName.setText(user.getName());
        editEmail.setText(user.getEmail());
        editTaille.setText(String.valueOf(user.getTaille()));
        editPoids.setText(String.valueOf(user.getPoids()));

        // Set spinner values
        setSpinnerValue(editSexe, user.getSexe());
        setSpinnerValue(editNiveau, user.getNiveau());
        setSpinnerValue(editHealthCondition, user.getHealthCondition());

        if (user.getImage_base64() != null) {
            byte[] decodedString = Base64.decode(user.getImage_base64(), Base64.DEFAULT);
            Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedByte);
            currentImageBase64 = user.getImage_base64();
        }
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }

    private void setupUpdateButton() {
        btnUpdate.setOnClickListener(v -> updateUserProfile(userId));
    }

    private void updateUserProfile(Long userId) {
        User updatedUser = new User();
        updatedUser.setName(editName.getText().toString());
        updatedUser.setEmail(editEmail.getText().toString());
        updatedUser.setSexe(editSexe.getSelectedItem().toString());
        updatedUser.setTaille(Float.parseFloat(editTaille.getText().toString()));
        updatedUser.setPoids(Float.parseFloat(editPoids.getText().toString()));
        updatedUser.setNiveau(editNiveau.getSelectedItem().toString());
        updatedUser.setHealthCondition(editHealthCondition.getSelectedItem().toString());
        updatedUser.setImage_base64(currentImageBase64);

        authService.updateUser(userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
