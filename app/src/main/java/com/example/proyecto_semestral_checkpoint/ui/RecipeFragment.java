package com.example.proyecto_semestral_checkpoint.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.adapters.RecipesRecyclerViewAdapter;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.models.User;
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

    private User user;
    private ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String token = settings.getString("token", "");

        Call<User> call = recipe_app_api.getUser("Bearer " + token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()) {
                    return;
                }
                user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecipes_With_RecyclerView();

        getView().findViewById(R.id.create_recipe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_recipeFragment_to_createRecipesFragment);
            }
        });
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
                RecipeFragmentDirections.ActionRecipeFragmentToViewRecipeFragment action = RecipeFragmentDirections.actionRecipeFragmentToViewRecipeFragment(recipe, user);
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
                    Toast.makeText(getActivity(), "Error while fetching list, please try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                recipes = response.body();
                adapter.setRecipesList(recipes);

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
