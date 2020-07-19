package com.example.proyecto_semestral_checkpoint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_semestral_checkpoint.models.Log_In_User;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText Email, Password;
    TextView Register;
    Button Login;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControllers();
    }

    private void initControllers() {
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Login = findViewById(R.id.btnLogin);
        Register = findViewById(R.id.btnRegister);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validEmail = !Email.getText().toString().isEmpty();
                boolean validPass = !Password.getText().toString().isEmpty();
                if(!validEmail) {
                    Email.requestFocus();
                    Email.setError("El email no puede estar vacío.");
                }
                if(!validPass) {
                    Password.requestFocus();
                    Password.setError("El password no puede estar vacío.");
                }
                if (validEmail && validPass){
                    login(Email.getText().toString(), Password.getText().toString());
                }
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });
    }

    private void login(String email, String password) {
        Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        Call<Log_In_User> call = recipe_app_api.login(user);
        call.enqueue(new Callback<Log_In_User>() {
            @Override
            public void onResponse(Call<Log_In_User> call, Response<Log_In_User> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Combinacion de Usuario y Contraseña no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log_In_User user = response.body();

                SharedPreferences settings = LoginActivity.this.getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();
                edit.putString("_id", user.getUser().getId());
                edit.putString("token", user.getToken());
                edit.putString("user_name", user.getUser().getName());
                edit.putString("email", user.getUser().getEmail());
                edit.apply();

                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
            }

            @Override
            public void onFailure(Call<Log_In_User> call, Throwable t) {

            }
        });
    }
}
