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

public class LoginViewModel extends ViewModel {
    private MutableLiveData<User> loginResultLiveData = new MutableLiveData<>();
    private AuthService authService;

    public LoginViewModel() {
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
    }

    public LiveData<User> getLoginResult() {
        return loginResultLiveData;
    }

    public void login(String email, String password) {
        User loginUser = new User(email, password);

        authService.login(loginUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    loginResultLiveData.setValue(response.body());
                } else {
                    loginResultLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loginResultLiveData.setValue(null);
                Log.d("onFailure", "onFailure: "+t.toString());
            }
        });
    }
}