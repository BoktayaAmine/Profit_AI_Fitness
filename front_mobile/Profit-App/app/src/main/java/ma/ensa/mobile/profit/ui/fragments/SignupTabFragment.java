package ma.ensa.mobile.profit.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.ui.UserDetailsActivity;
import ma.ensa.mobile.profit.viewmodels.SignupViewModel;

public class SignupTabFragment extends Fragment {
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button signupButton;
    private SignupViewModel signupViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        nameEditText = view.findViewById(R.id.username);
        emailEditText = view.findViewById(R.id.signup_email);
        passwordEditText = view.findViewById(R.id.signup_password);
        signupButton = view.findViewById(R.id.signup_button);
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        signupButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            Log.d("SignupTabFragment", "Button clicked. Name: " + name + ", Email: " + email);


            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                signupViewModel.signup(name, email, password);
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        signupViewModel.getSignupResult().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Log.d("SignupTabFragment", "Signup successful. User: " + user.toString());
                Toast.makeText(getContext(), "Signup successful!", Toast.LENGTH_SHORT).show();

                // Naviguer vers UserDetailsActivity
                Intent intent = new Intent(getContext(), UserDetailsActivity.class);
                intent.putExtra("USER_ID", user.getId()); // Transmettre l'ID utilisateur
                startActivity(intent);
                requireActivity().finish(); // Terminer l'activité actuelle
            } else {
                Toast.makeText(getContext(), "Signup failed. Email might already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
