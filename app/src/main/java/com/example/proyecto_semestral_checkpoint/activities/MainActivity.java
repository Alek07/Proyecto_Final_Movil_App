package com.example.proyecto_semestral_checkpoint.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;

    private User user;

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);

        init();
        userLogged();
        Listeners();
    }

    //Set logged user info
    private void userLogged() {
        SharedPreferences settings = getSharedPreferences("User", MODE_PRIVATE);
        String token = settings.getString("token", "");

        Call<User> call = recipe_app_api.getUser("Bearer " + token);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Something went fetching user info", Toast.LENGTH_SHORT).show();
                    Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(logout);
                    return;
                }

                user = response.body();
                setUserInfo();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserInfo() {
        TextView User_name =  headerView.findViewById(R.id.user_name);
        TextView Email =  headerView.findViewById(R.id.email);
        ImageView Image =  headerView.findViewById(R.id.profile_image);


        User_name.setText(user.getName());
        Email.setText(user.getEmail());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + user.getId() + "/avatar").apply(options).into(Image);
    }


    private void Listeners() {
        //Control the action of opening and closing the drawer
        findViewById(R.id.menu_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //Setup the navigation components with the drawer layout
    private void init() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.homeFragment);
    }

    //Manage the navigation, with Nav Component, when an option in the drawer is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.homeNav: {

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.my_nav, true)
                        .build();

                Bundle bundle =  new Bundle();
                bundle.putSerializable("user", user);
                Log.d("USER", "onNavigationItemSelected: " + user);
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.homeFragment, bundle, navOptions);

                break;
            }
            case R.id.recipeNav: {

                if(isValidDestination(R.id.recipeFragment)) {
                    Bundle bundle =  new Bundle();
                    bundle.putSerializable("user", user);
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.recipeFragment, bundle);
                }
                break;
            }
            case R.id.favoriteNav: {

                if(isValidDestination(R.id.favoritesFragment)) {
                    Bundle bundle =  new Bundle();
                    bundle.putSerializable("user", user);
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.favoritesFragment, bundle);
                }
                break;
            }
            case R.id.profileNav: {

                if(isValidDestination(R.id.profileFragment)) {

                    Bundle bundle =  new Bundle();
                    bundle.putSerializable("user", user);
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.profileFragment, bundle);
                }
                break;

            }
            case R.id.logout: {
                SharedPreferences settings = getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor edit = settings.edit();

                String token = settings.getString("token", "");

                Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
                Call<User> call = recipe_app_api.logout("Bearer " + token);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()) {
                            return;
                        }

                        user = null;

                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                edit.putBoolean("isLogged", false);
                edit.apply();

                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                break;
            }

        }

        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Check the back stack if a fragment is displayed
    private boolean isValidDestination(int destination) {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId();
    }

    //Binds the navigation menu with the drawer action
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
