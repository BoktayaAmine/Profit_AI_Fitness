package ma.ensa.mobile.profit.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.ui.UserDetailsActivity;
import ma.ensa.mobile.profit.ui.WelcomActivity;
import ma.ensa.mobile.profit.viewmodels.LoginViewModel;

public class LoginTabFragment extends Fragment {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        emailEditText = view.findViewById(R.id.login_email);
        passwordEditText = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);



        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginViewModel.login(email, password);
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                // Navigate to the next screen
                Intent intent = new Intent(getContext(), WelcomActivity.class);
                intent.putExtra("USER_ID", user.getId()); // Transmettre l'ID utilisateur
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}