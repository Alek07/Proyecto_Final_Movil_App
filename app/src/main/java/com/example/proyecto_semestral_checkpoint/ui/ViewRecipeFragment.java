package com.example.proyecto_semestral_checkpoint.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
public class ViewRecipeFragment extends Fragment {

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
    private String[] categories = new String[]{"Soup", "Appetizer", "Salads", "Breads ", "Drinks", "Desserts", "Main Dish", "Side Dish"};

    private TextView Name, Ingredients, Description, Category;
    private EditText NameE, IngredientsE, DescriptionE;
    private ImageView Edit, Cancel;
    private Spinner Categories;
    private Button Save, Delete;
    private Recipe recipe;

    public ViewRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewRecipeFragmentArgs args = ViewRecipeFragmentArgs.fromBundle(getArguments());
        recipe = args.getRecipe();
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeControls();
        setRecipeInfo();
        toggleEditMode();
        validateInputs();
    }

    private void initializeControls(){
        Name = getView().findViewById(R.id.name);
        Ingredients = getView().findViewById(R.id.ingredients);
        Description = getView().findViewById(R.id.description);
        Category = getView().findViewById(R.id.category);

        NameE = getView().findViewById(R.id.name_edit);
        IngredientsE = getView().findViewById(R.id.ingredients_edit);
        DescriptionE = getView().findViewById(R.id.description_edit);
        Categories = getView().findViewById(R.id.category_edit);

        Edit = getView().findViewById(R.id.edit);
        Cancel = getView().findViewById(R.id.cancel);

        Save = getView().findViewById(R.id.btnSave);
        Delete = getView().findViewById(R.id.btnDelete);

        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        Categories.setAdapter(adapter);
    }

    private void toggleEditMode() {
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setVisibility(View.GONE);
                Ingredients.setVisibility(View.GONE);
                Description.setVisibility(View.GONE);
                Category.setVisibility(View.GONE);

                NameE.setVisibility(View.VISIBLE);
                IngredientsE.setVisibility(View.VISIBLE);
                DescriptionE.setVisibility(View.VISIBLE);
                Categories.setVisibility(View.VISIBLE);

                Edit.setVisibility(View.GONE);
                Cancel.setVisibility(View.VISIBLE);

                Save.setVisibility(View.VISIBLE);
                Delete.setVisibility(View.VISIBLE);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setVisibility(View.VISIBLE);
                Ingredients.setVisibility(View.VISIBLE);
                Description.setVisibility(View.VISIBLE);
                Category.setVisibility(View.VISIBLE);

                NameE.setVisibility(View.GONE);
                IngredientsE.setVisibility(View.GONE);
                DescriptionE.setVisibility(View.GONE);
                Categories.setVisibility(View.GONE);

                Edit.setVisibility(View.VISIBLE);
                Cancel.setVisibility(View.GONE);

                Save.setVisibility(View.GONE);
                Delete.setVisibility(View.GONE);
            }
        });
    }

    private void setRecipeInfo() {
        String category = "";
        ArrayList<HashMap<String, String>> lstingredients = recipe.getIngredients();
        String ingredients ="";

        //Convert category id to String
        for(int i = 0; i < categories.length; i++){
            if(i + 1 == recipe.getCategory())
                category = categories[i];
        }

        //Convert list of ingredients to a String
        for(int j = 0; j < lstingredients.size(); j++) {
            if(j + 1 == lstingredients.size())
                ingredients += lstingredients.get(j).get("ingredient");
            else
                ingredients += lstingredients.get(j).get("ingredient") + ",";
        }

        Name.setText(recipe.getName());
        Ingredients.setText(ingredients);
        Description.setText(recipe.getDescription());
        Category.setText(category);

        NameE.setText(recipe.getName());
        IngredientsE.setText(ingredients);
        DescriptionE.setText(recipe.getDescription());

    }

    private void validateInputs() {
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidName = !NameE.getText().toString().isEmpty();
                boolean isValidDescription = !DescriptionE.getText().toString().isEmpty();
                boolean isValidIngredients = !IngredientsE.getText().toString().isEmpty();

                ArrayList<HashMap<String, String>> ingredients = new ArrayList<>();

                //Check if their'r empty
                if(!isValidName) {
                    NameE.requestFocus();
                    NameE.setError("El nombre no puede estar vacío.");
                }
                if(!isValidDescription) {
                    DescriptionE.requestFocus();
                    DescriptionE.setError("La descripcion no puede estar vacío.");
                }
                if(!isValidIngredients) {
                    IngredientsE.requestFocus();
                    IngredientsE.setError("Los ingredientes no puede estar vacío.");
                } else {

                    //Check ingredients
                    String[] newIngredients = IngredientsE.getText().toString().split(",");

                    if(newIngredients.length > 5) {
                        isValidIngredients = false;
                        IngredientsE.requestFocus();
                        IngredientsE.setError("No puede agregar mas de 5 ingredientes");
                    }

                    //Add ingredients on array list
                    for(int j = 0; j < newIngredients.length; j++) {
                        HashMap<String, String> index = new HashMap<>();
                        index.put("ingredient", newIngredients[j]);
                        ingredients.add(index);
                    }

                    Log.d("INGREDIENTS", "onClick: " + ingredients);
                }

                //Convert category
                int category = 0;
                for(int i = 0; i < categories.length; i++){
                    if(Categories.getSelectedItem().equals(categories[i])) {
                        category = i + 1;
                        Log.d("CATEGORY", "onClick: " + category);
                    }
                }

                if(isValidName && isValidDescription && isValidIngredients) {
                    Log.d("REQUEST", "onClick: REQUEST CALL INICIALIZE");
                    updateRecipeRequest(ingredients, category);
                }

            }
        });
    }

    private void updateRecipeRequest(ArrayList<HashMap<String, String>> ingredients, int category) {
        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String author = settings.getString("_id", "null");
        String token = settings.getString("token", "");

        Recipe new_recipe = new Recipe(
                NameE.getText().toString(),
                category,
                DescriptionE.getText().toString(),
                null,
                ingredients
        );

        Log.d("RECIPE ID TO UPDATE", "updateRecipeRequest: " + new_recipe);
        Call<Recipe> call = recipe_app_api.updateRecipe(recipe.get_id().toString(),"Bearer " + token, new_recipe);

        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if(!response.isSuccessful()) {
                    Log.d("REQUEST NO SUCCESSFUL", "onResponse: " + response.code());
                    Log.d("RESPONSE BODY", "onResponse: " + response.body());
                    return;
                }

                recipe = response.body();

                refresh();
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {

            }
        });
    }

    public void deleteRecipe() {
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void refresh() {
        Name.setVisibility(View.VISIBLE);
        Ingredients.setVisibility(View.VISIBLE);
        Description.setVisibility(View.VISIBLE);
        Category.setVisibility(View.VISIBLE);

        NameE.setVisibility(View.GONE);
        IngredientsE.setVisibility(View.GONE);
        DescriptionE.setVisibility(View.GONE);
        Categories.setVisibility(View.GONE);

        Edit.setVisibility(View.VISIBLE);
        Cancel.setVisibility(View.GONE);

        Save.setVisibility(View.GONE);
        Delete.setVisibility(View.GONE);

        onStart();
    }


}
