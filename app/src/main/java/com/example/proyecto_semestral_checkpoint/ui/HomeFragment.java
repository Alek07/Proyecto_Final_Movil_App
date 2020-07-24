package com.example.proyecto_semestral_checkpoint.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.adapters.CategoryMenuAdapter;
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
public class HomeFragment extends Fragment {

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
    private String[] categories = new String[]{"Sopas", "Aperitivos", "Ensaladas", "Panes ", "Bebidas", "Postres", "Entradas", "Guarniciones"};

    private User user;
    private ArrayList<Recipe> recipes = new ArrayList<>();

    private ProgressBar progressBar;

    private ArrayList<String> cNames = new ArrayList<>();
    private ArrayList<Integer> cImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
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
                Toast.makeText(getActivity(), "Hubo un error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!cNames.isEmpty()) {
            cNames.clear();
            cImages.clear();
        }

        progressBar = getView().findViewById(R.id.loading_list);

        initCategoryMenu();

        initRecipes_With_RecyclerView();
    }

    private void initCategoryMenu() {
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

        adapter.setOnItemClickListener(new CategoryMenuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    int category = 1;
                    String cName = cNames.get(position);

                    for(int i = 0; i < categories.length; i++) {
                        if(categories[i].equals(cName))
                            category = i + 1;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt("category",category);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_categoryFragment, bundle);
                } catch (Exception e) {
                    Log.d("EXCEPTION", "onItemClick: " + e);
                }
            }
        });
    }

    private void initRecipes_With_RecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = getView().findViewById(R.id.recipes_view);
        recyclerView.setLayoutManager(layoutManager);
        RecipesRecyclerViewAdapter adapter = new RecipesRecyclerViewAdapter(getContext(), recipes);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecipesRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    Recipe recipe = recipes.get(position);
                    HomeFragmentDirections.ActionHomeFragmentToViewRecipeFragment action = HomeFragmentDirections.actionHomeFragmentToViewRecipeFragment(user, recipe);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                } catch(Exception e) {
                    Log.d("EDIT RECIPE SCREEN", "onItemClick: " + e);
                }
            }
        });

        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String token = settings.getString("token", "");

        Call<ArrayList<Recipe>> call = recipe_app_api.getRecipe("Bearer " + token, null);

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Error al cargar la lista, intentelo nuevamente", Toast.LENGTH_SHORT).show();
                    return;
                }

                recipes = response.body();
                adapter.setRecipesList(recipes);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Toast.makeText(getActivity(), "Hubo un error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
