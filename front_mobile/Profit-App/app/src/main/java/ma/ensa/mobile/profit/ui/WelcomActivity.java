package ma.ensa.mobile.profit.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.databinding.ActivityWelcomBinding;
import ma.ensa.mobile.profit.models.User;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import ma.ensa.mobile.profit.ui.fragments.GoalFragment;
import ma.ensa.mobile.profit.ui.fragments.ObjectifFragment;
import ma.ensa.mobile.profit.ui.fragments.ProfileFragment;
import ma.ensa.mobile.profit.ui.fragments.WorkoutFragment;
import ma.ensa.mobile.profit.ui.Home;  // Import the Home fragment
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityWelcomBinding binding;
    private DrawerLayout drawerLayout;
    private Long userId;
    private AuthService authService;
    private TextView navUsername,navEmail;
    private ImageView navImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize user ID from intent
        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user ID is passed
        }

        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);


        // Set up Toolbar
        setSupportActionBar(binding.toolbar);
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);


        // Get the header view
        View headerView = navigationView.getHeaderView(0);

        // Initialize header views
        navImage = headerView.findViewById(R.id.nav_header_image);
        navUsername = headerView.findViewById(R.id.nav_header_name);
        navEmail = headerView.findViewById(R.id.nav_header_email);

        loadUserData(userId);


        // Configure ActionBarDrawerToggle for Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize with Home Fragment as the default view
        if (savedInstanceState == null) {
            replaceFragment(new Home());  // Replaced HomeFragment with Home
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Handle BottomNavigationView item selection
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new Home());  // Replaced HomeFragment with Home
                    break;
                case R.id.workouts:
                    replaceFragment(new WorkoutFragment());
                    break;
                case R.id.objectifs:
                    replaceFragment(new ObjectifFragment());
                    break;
                case R.id.tools:
                    replaceFragment(new GoalFragment(userId));
                    break;
            }
            return true;
        });

        // Handle FloatingActionButton click
        binding.floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomActivity.this, ExerciseActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    // Handle Navigation Drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new Home());  // Replaced HomeFragment with Home
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(WelcomActivity.this, ProfileActivity.class);
                intent.putExtra("USER_ID", userId); // Pass user ID if needed
                startActivity(intent);
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show();
                finish(); // Optional: Close the activity on logout
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handle back button to close the navigation drawer if open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Helper method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
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
                Toast.makeText(WelcomActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(User user) {
        navUsername.setText(user.getName());
        navEmail.setText(user.getEmail());


        if (user.getImage_base64() != null) {
            byte[] decodedString = Base64.decode(user.getImage_base64(), Base64.DEFAULT);
            Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this)
                    .load(decodedByte) // Pass the decoded bitmap directly
                    .circleCrop() // Optional: for circular image
                    .into(navImage);
        }
    }
}
