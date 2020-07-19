package com.example.proyecto_semestral_checkpoint.network;

import com.example.proyecto_semestral_checkpoint.models.Log_In_User;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Recipe_App_API {

    @POST("users")
    Call<User> register(@Body User user);

    @POST("users/login")
    Call<Log_In_User> login(@Body User user);

    @POST("users/logoutAll")
    Call<User> logout(@Header("Authorization") String token);

    @GET("recipes")
    Call<ArrayList<Recipe>> getRecipe(@Header("Authorization") String token);

    @GET("users/me/recipes")
    Call<ArrayList<Recipe>> getUserRecipe(@Header("Authorization") String token);

    @GET("users/me/favorites")
    Call<ArrayList<Recipe>> getUserFavorites(@Header("Authorization") String token);
}
