package com.example.proyecto_semestral_checkpoint.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateRecipesFragment extends Fragment {

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
    private String[] categories = new String[]{"Soup", "Appetizer", "Salads", "Breads ", "Drinks", "Desserts", "Main Dish", "Side Dish"};

    private EditText Name, Ingredients, Description;
    private Spinner Categories;
    private Button Save;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_recipes, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initControllers();
        setupSpinner();
        validateInputs();
    }

    private void initControllers() {
        Name = getView().findViewById(R.id.name);
        Ingredients = getView().findViewById(R.id.ingredients);
        Description = getView().findViewById(R.id.description);
        Categories = getView().findViewById(R.id.category);
        Save = getView().findViewById(R.id.save);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        Categories.setAdapter(adapter);
    }

    private void validateInputs() {
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidName = !Name.getText().toString().isEmpty();
                boolean isValidDescription = !Description.getText().toString().isEmpty();
                boolean isValidIngredients = !Ingredients.getText().toString().isEmpty();

                ArrayList<HashMap<String, String>> ingredients = new ArrayList<>();

                //Check if their'r empty
                if(!isValidName) {
                    Name.requestFocus();
                    Name.setError("Recipe Name can't be empty");
                }
                if(!isValidDescription) {
                    Description.requestFocus();
                    Description.setError("Description can't be empty");
                }
                if(!isValidIngredients) {
                    Ingredients.requestFocus();
                    Ingredients.setError("Ingredients can't be empty");
                } else {

                    //Check ingredients
                    String[] newIngredients = Ingredients.getText().toString().split(",");

                    if(newIngredients.length > 6) {
                        isValidIngredients = false;
                        Ingredients.requestFocus();
                        Ingredients.setError("You can't add more than 6 ingredients");
                    }

                    //Add ingredients on array list
                    for(int j = 0; j < newIngredients.length; j++) {
                        HashMap<String, String> index = new HashMap<>();
                        index.put("ingredient", newIngredients[j]);
                        ingredients.add(index);
                    }
                }

                //Convert category
                int category = 0;
                for(int i = 0; i < categories.length; i++){
                    if(Categories.getSelectedItem().equals(categories[i])) {
                        category = i + 1;
                    }
                }

                if(isValidName && isValidDescription && isValidIngredients) {
                    Save.setEnabled(false);
                    createRecipesRequest(ingredients, category);
                }

            }
        });
    }

    private void createRecipesRequest(ArrayList<HashMap<String, String>> ingredients, int category) {
        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String author = settings.getString("_id", "null");
        String token = settings.getString("token", "");

        Recipe recipe = new Recipe(
                Name.getText().toString(),
                category,
                Description.getText().toString(),
                author,
                ingredients,
                null
        );

        Call<Recipe> call = recipe_app_api.createRecipe("Bearer " + token, recipe);

        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if(!response.isSuccessful()) {
                    Save.setEnabled(true);
                    Toast.makeText(getActivity(), "Error while creating recipe, make sure your data is correct", Toast.LENGTH_SHORT).show();
                    return;
                }

                Recipe new_recipe = response.body();
                if(new_recipe != null)
                    Save.setEnabled(true);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_createRecipesFragment_to_recipeFragment);
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Save.setEnabled(true);
                Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
