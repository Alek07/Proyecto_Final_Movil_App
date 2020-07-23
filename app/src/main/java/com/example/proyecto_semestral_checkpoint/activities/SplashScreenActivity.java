package com.example.proyecto_semestral_checkpoint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_semestral_checkpoint.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences("User", MODE_PRIVATE);
                boolean isLogged = settings.getBoolean("isLogged", false);

                if(!isLogged) {
                    Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    Intent loginIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
