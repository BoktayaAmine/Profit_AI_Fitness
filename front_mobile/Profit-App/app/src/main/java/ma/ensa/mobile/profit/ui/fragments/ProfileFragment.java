package ma.ensa.mobile.profit.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import ma.ensa.mobile.profit.R;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName, profileEmail;
    private Button btnEditProfile, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        setupProfile();

        btnEditProfile.setOnClickListener(v -> {
            // Handle edit profile logic
        });

        btnLogout.setOnClickListener(v -> {
            // Handle logout logic
        });

        return view;
    }

    private void setupProfile() {
        // Placeholder for now
        profileName.setText("John Doe");
        profileEmail.setText("johndoe@example.com");

        // Load profile image using Glide (replace with actual URL if needed)
        Glide.with(this)
                .load(R.drawable.user) // Replace with dynamic URL later
                .circleCrop()
                .into(profileImage);
    }
}
