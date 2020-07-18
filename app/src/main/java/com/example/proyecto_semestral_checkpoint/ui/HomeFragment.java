package com.example.proyecto_semestral_checkpoint.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.adapter.CategoryMenuAdapter;
import com.example.proyecto_semestral_checkpoint.adapter.RecipesRecyclerViewAdapter;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Recipe_App_API recipe_app_api;
    ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<String> cNames = new ArrayList<>();
    private ArrayList<Integer> cImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!cNames.isEmpty()) {
            cNames.clear();
            cImages.clear();
        }

        initCategoryMenu();

        setupRetrofit();
        initRecipes_With_RecyclerView();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://recetas-api-v1.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        recipe_app_api = retrofit.create(Recipe_App_API.class);
    }

    private void initCategoryMenu() {
        String[] categories = new String[]{"Soup", "Appetizer", "Salads", "Breads ", "Drinks", "Desserts", "Main Dish", "Side Dish"};
        int[] categories_images = new int[]{R.drawable.soup, R.drawable.appetizer, R.drawable.salad, R.drawable.bread, R.drawable.drinks, R.drawable.dessert, R.drawable.main_dish, R.drawable.side_dish};

        for(int i = 0; i < categories.length; i++){
            cNames.add(categories[i]);
            cImages.add(categories_images[i]);
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = getView().findViewById(R.id.category_view);
        recyclerView.setLayoutManager(layoutManager);
        CategoryMenuAdapter adapter = new CategoryMenuAdapter(getContext(), cNames, cImages);
        recyclerView.setAdapter(adapter);
    }

    private void initRecipes_With_RecyclerView() {



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = getView().findViewById(R.id.recipes_view);
        recyclerView.setLayoutManager(layoutManager);
        RecipesRecyclerViewAdapter adapter = new RecipesRecyclerViewAdapter(getContext(), recipes);
        recyclerView.setAdapter(adapter);

        Call<ArrayList<Recipe>> call = recipe_app_api.getRecipe("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZjBlMmU1ZDMxOGZkMjAwMTc2NTI4MWYiLCJpYXQiOjE1OTUwMzEwOTF9._hooiwDTgVQ40piTwNwsoLn58GEhiETM2s9_kHsLF0E");

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if(!response.isSuccessful()) {
                    return;
                }

                recipes = response.body();
                adapter.setRecipesList(recipes);

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {

            }
        });
    }
}
