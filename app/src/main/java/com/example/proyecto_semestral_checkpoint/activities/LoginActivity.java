package com.example.proyecto_semestral_checkpoint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.Log_In_User;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;
import com.example.proyecto_semestral_checkpoint.util.LoadingDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText Email, Password;
    private TextView Register;
    private Button Login;

    private LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);


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
                    Email.setError("El correo no puede estar vacio");
                }
                if(!validPass) {
                    Password.requestFocus();
                    Password.setError("La contraseña no puede estar vacia");
                }
                if (validEmail && validPass){
                    loadingDialog.startLoading();
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
                    loadingDialog.dismissDialog();
                    Toast.makeText(LoginActivity.this, "Correo o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log_In_User log_in_info = response.body();

                if(log_in_info != null) {
                    User user = log_in_info.getUser();

                    SharedPreferences settings = LoginActivity.this.getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString("_id", user.getId());
                    edit.putString("token", log_in_info.getToken());
                    edit.putBoolean("isLogged", true);
                    edit.apply();

                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    loadingDialog.dismissDialog();
                    startActivity(main);
                }
            }

            @Override
            public void onFailure(Call<Log_In_User> call, Throwable t) {
                loadingDialog.dismissDialog();
                Toast.makeText(LoginActivity.this, "Hubo un error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
