package com.example.proyecto_semestral_checkpoint.network;

import com.example.proyecto_semestral_checkpoint.models.Log_In_User;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.models.User;

import java.io.File;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Recipe_App_API {

    @POST("users")
    Call<User> register(@Body User user);

    @POST("users/login")
    Call<Log_In_User> login(@Body User user);

    @POST("users/logoutAll")
    Call<User> logout(@Header("Authorization") String token);

    @Multipart
    @POST("users/me/avatar")
    Call<ResponseBody> uploadUserImage(@Header("Authorization") String token, @Part("avatar") File image);

    @GET("recipes")
    Call<ArrayList<Recipe>> getRecipe(@Header("Authorization") String token, @Query("category") Integer category);

    @POST("recipes")
    Call<Recipe> createRecipe(@Header("Authorization") String token, @Body Recipe recipe);

    @PATCH("recipes/{id}")
    Call<Recipe> updateRecipe(@Path("id") String id, @Header("Authorization") String token, @Body Recipe recipe);

    @PATCH("recipes/{id}/add")
    Call<User> addFavorites(@Path("id") String id, @Header("Authorization") String token);

    @PATCH("recipes/{id}/remove")
    Call<User> removeFavorites(@Path("id") String id, @Header("Authorization") String token);

    @DELETE("recipes/{id}")
    Call<Recipe> deleteRecipe(@Path("id") String id, @Header("Authorization") String token);

    @GET("users/me")
    Call<User> getUser(@Header("Authorization") String token);

    @PATCH("users/me")
    Call<User> updateUser(@Header("Authorization") String token, @Body User user);

    @GET("users/me/recipes")
    Call<ArrayList<Recipe>> getUserRecipe(@Header("Authorization") String token);

    @GET("users/me/favorites")
    Call<ArrayList<Recipe>> getUserFavorites(@Header("Authorization") String token);
}
