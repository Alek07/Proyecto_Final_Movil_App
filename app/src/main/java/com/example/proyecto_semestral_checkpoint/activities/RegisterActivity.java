package com.example.proyecto_semestral_checkpoint.activities;

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

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;
import com.example.proyecto_semestral_checkpoint.util.LoadingDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText Name, Email, Password;
    private Button Register;
    private TextView Login;

    private LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);



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
                    Name.setError("El nombre de usuario no puede estar vacio");
                }
                if (!validEmail) {
                    Email.requestFocus();
                    Email.setError("El correo no puede estar vacio");
                }
                if (!validPass) {
                    Password.requestFocus();
                    Password.setError("La contraseña no puede estar vacia");
                } else if(Password.length() < 9) {
                    validPass = false;
                    Password.requestFocus();
                    Password.setError("La contraseña debe tener mas de 9 caracteres");
                }


                if (validName && validEmail && validPass) {
                    Register.setEnabled(false);
                    loadingDialog.startLoading();
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
                    Register.setEnabled(true);
                    loadingDialog.dismissDialog();
                    Toast.makeText(RegisterActivity.this, "Error de registro, Porfavor verifica los campos nuevamente", Toast.LENGTH_SHORT).show();
                    return;
                }

                Register.setEnabled(true);
                loadingDialog.dismissDialog();
                Intent register = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(register);
                Toast.makeText(RegisterActivity.this, "Usuario registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Register.setEnabled(true);
                loadingDialog.dismissDialog();
                Toast.makeText(RegisterActivity.this, "Hubo un error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
