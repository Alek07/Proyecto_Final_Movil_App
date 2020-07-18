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

    @POST("users/login")
    Call<Log_In_User> login(@Body User user);

    @GET("recipes")
    Call<ArrayList<Recipe>> getRecipe(@Header("Authorization") String token);
}
