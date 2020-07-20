package com.example.proyecto_semestral_checkpoint.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.adapter.CategoryMenuAdapter;
import com.example.proyecto_semestral_checkpoint.adapter.RecipesRecyclerViewAdapter;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment {

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
    ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecipes_With_RecyclerView();
    }

    private void initRecipes_With_RecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = getView().findViewById(R.id.user_recipes_view);
        recyclerView.setLayoutManager(layoutManager);
        RecipesRecyclerViewAdapter adapter = new RecipesRecyclerViewAdapter(getContext(), recipes);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecipesRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Recipe recipe = recipes.get(position);
                RecipeFragmentDirections.ActionRecipeFragmentToViewRecipeFragment action = RecipeFragmentDirections.actionRecipeFragmentToViewRecipeFragment(recipe);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String token = settings.getString("token", "");

        Call<ArrayList<Recipe>> call = recipe_app_api.getUserRecipe("Bearer " + token);

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
