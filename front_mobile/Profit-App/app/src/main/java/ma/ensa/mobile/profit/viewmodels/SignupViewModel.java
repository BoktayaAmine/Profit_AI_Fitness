package ma.ensa.mobile.profit.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ma.ensa.mobile.profit.models.User;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupViewModel extends ViewModel {
    private MutableLiveData<User> signupResultLiveData = new MutableLiveData<>();
    private AuthService authService;

    public SignupViewModel() {
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
    }

    public LiveData<User> getSignupResult() {
        return signupResultLiveData;
    }

    public void signup(String name, String email, String password) {
        User newUser = new User(name, email, password);
        // Ajoutez le nom si nécessaire dans votre modèle User

        authService.signup(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    signupResultLiveData.setValue(response.body());
                } else {
                    signupResultLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                signupResultLiveData.setValue(null);
                Log.d("onFailure", "onFailure: "+t.toString());
            }
        });
    }
}