package com.example.proyecto_semestral_checkpoint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText Name, Email, Password;
    Button Register;
    TextView Login;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initControllers();
    }

    private void initControllers() {
        Name = findViewById(R.id.user_name);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Login = findViewById(R.id.btnLogin);
        Register = findViewById(R.id.btnRegister);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validName = !Name.getText().toString().isEmpty();
                boolean validEmail = !Email.getText().toString().isEmpty();
                boolean validPass = !Password.getText().toString().isEmpty();
                if (!validName) {
                    Name.requestFocus();
                    Name.setError("El nombre de usuario no puede estar vacío.");
                }
                if (!validEmail) {
                    Email.requestFocus();
                    Email.setError("El email no puede estar vacío.");
                }
                if (!validPass) {
                    Password.requestFocus();
                    Password.setError("El password no puede estar vacío.");
                }
                if (validName && validEmail && validPass) {
                    register(Name.getText().toString(), Email.getText().toString(), Password.getText().toString());
                }
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(register);
            }
        });
    }

    private void register(String name, String email, String password) {
        Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        Call<User> call = recipe_app_api.register(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Error al registrarse, por favor vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = response.body();
                Log.d("USER", "onResponse: " + user.getName());
                Intent register = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(register);
                Toast.makeText(RegisterActivity.this, "Usuario Registrado exitosamente !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
