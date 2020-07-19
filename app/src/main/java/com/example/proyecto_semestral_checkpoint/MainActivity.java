package com.example.proyecto_semestral_checkpoint;

import androidx.annotation.NonNull;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    View headerView;
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
    }

    //Control the action of opening and closing the drawer
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home: {
//                if(drawerLayout.isDrawerOpen((GravityCompat.START))) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                    return true;
//                }
//                else
//                    return false;
//            }
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    //Manage the navigation, with Nav Component, when an option in the drawer is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.homeNav: {

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.my_nav, true)
                        .build();

                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.homeFragment, null, navOptions);

                break;
            }
            case R.id.recipeNav: {

                if(isValidDestination(R.id.recipeFragment))
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.recipeFragment);

                break;
            }
            case R.id.favoriteNav: {

                if(isValidDestination(R.id.favoritesFragment))
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.favoritesFragment);

                break;
            }
            case R.id.logout: {
                SharedPreferences settings = getSharedPreferences("User", MODE_PRIVATE);
                String token = settings.getString("token", "");

                Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
                Call<User> call = recipe_app_api.logout("Bearer " + token);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()) {
                            return;
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                break;
            }

        }

        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Check the backstack if a fragment is displayed
    private boolean isValidDestination(int destination) {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId();
    }

    //Binds the navigation menu with the drawer action
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    //Set logged user info
    private void userLogged() {
        TextView User_name =  headerView.findViewById(R.id.user_name);
        TextView Email =  headerView.findViewById(R.id.email);
        ImageView Image =  headerView.findViewById(R.id.profile_image);

        SharedPreferences settings = getSharedPreferences("User", MODE_PRIVATE);
        String userName = settings.getString("user_name", "User Name");
        String email = settings.getString("email", "User Name");
        String id = settings.getString("_id", "no id");

        User_name.setText(userName);
        Email.setText(email);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + id + "/avatar").apply(options).into(Image);
    }
}
